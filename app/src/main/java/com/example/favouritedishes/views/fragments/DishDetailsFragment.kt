package com.example.favouritedishes.views.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.favouritedishes.R
import com.example.favouritedishes.App
import com.example.favouritedishes.databinding.FragmentDishDetailsBinding
import com.example.favouritedishes.models.models.Dish
import com.example.favouritedishes.utils.Constants
import com.example.favouritedishes.viewmodels.DishViewModel
import com.example.favouritedishes.viewmodels.DishViewModelFactory
import java.io.IOException

class DishDetailsFragment : Fragment() {

    private var binding : FragmentDishDetailsBinding? = null
    private val viewModel: DishViewModel by viewModels {
        DishViewModelFactory((requireActivity().application as App).repository)
    }
    private var dish: Dish? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDishDetailsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        setViews()
    }

    private fun setViews(){
        val args: DishDetailsFragmentArgs by navArgs()
        dish = args.dishDetails

        args.let {
            try {
                Glide.with(requireActivity())
                    .load(it.dishDetails.image)
                    .centerCrop()
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("TAG", "ERROR loading image")
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            resource.let {
                                Palette.from(resource!!.toBitmap()).generate { palette ->
                                    val color = palette?.vibrantSwatch?.rgb ?: 0
                                    binding!!.scrollviewDetails.setBackgroundColor(color)
                                }
                            }
                            return false
                        }
                    })
                    .into(binding!!.ivDishImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            binding!!.tvTitle.text = it.dishDetails.title
            binding!!.tvType.text = it.dishDetails.type
            binding!!.tvCategory.text = it.dishDetails.category
            binding!!.tvIngredients.text = it.dishDetails.ingredients

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                binding!!.tvCookingDirection.text = Html.fromHtml(it.dishDetails.direction, Html.FROM_HTML_MODE_COMPACT)
            else
                binding!!.tvCookingDirection.text = Html.fromHtml(it.dishDetails.direction)

            binding!!.tvCookingTime.text = resources.getString(R.string.estimate_cooking_time, it.dishDetails.cookingTime)

            if (args.dishDetails.fav){
                binding!!.ivHeart.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_favourite_selected))
                Toast.makeText(requireActivity(), resources.getString(R.string.favourite_added), Toast.LENGTH_SHORT).show()
            }else
                binding!!.ivHeart.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_favourite_unselected))
        }
        binding!!.ivHeart.setOnClickListener {
            args.dishDetails.fav = !args.dishDetails.fav
            viewModel.updateDishDetails(args.dishDetails)
            if (args.dishDetails.fav){
                binding!!.ivHeart.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_favourite_selected))
                Toast.makeText(requireActivity(), resources.getString(R.string.favourite_added), Toast.LENGTH_SHORT).show()
            }else
                binding!!.ivHeart.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_favourite_unselected))
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_share -> {
                val type = "text/plain"
                val subject = "Check this dish recipe!"
                var extraText = ""
                val shareWith = "Share With"

                dish?.let {
                    var image = ""
                    if (it.imageSource == Constants.SOURCE_ONLINE)
                        image = it.image
                    var instructions = ""
                    instructions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        Html.fromHtml(it.direction, Html.FROM_HTML_MODE_COMPACT).toString()
                    else
                        Html.fromHtml(it.direction).toString()
                    extraText = "$image \n" +
                            "\n Title:  ${it.title} \n\n Type: ${it.type} \n\n Category: ${it.category}" +
                            "\n\n Ingredients: \n ${it.ingredients} \n\n Instructions To Cook: \n $instructions" +
                            "\n\n Time required to cook the dish approx ${it.cookingTime} minutes."
                }
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = type
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                intent.putExtra(Intent.EXTRA_TEXT, extraText)
                startActivity(Intent.createChooser(intent, shareWith))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}