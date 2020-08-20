package com.akmere.iddog.ui.dogfeed

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.akmere.iddog.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeTransition
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.transition.TransitionFactory
import kotlin.reflect.KFunction2


class DogFeedAdapter(
    private val dogImages: List<String>,
    private val expandDogImage: KFunction2<Drawable?, View, Unit>
) :
    RecyclerView.Adapter<DogItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DogItemViewHolder {
        val dogItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.dog_feed_item, parent, false)
        return DogItemViewHolder(dogItem)
    }

    override fun onBindViewHolder(holder: DogItemViewHolder, position: Int) {
        val dogImageView = holder.dogItem.findViewById<AppCompatImageView>(R.id.dog_image)
        Glide
            .with(holder.itemView.context)
            .load(dogImages[position])
            .fitCenter()
            .transition(DrawableTransitionOptions.with(DrawableAlwaysCrossFadeFactory()))
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (dogImageView.hasOnClickListeners())
                        dogImageView.setOnClickListener { }
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    dogImageView.setOnClickListener {
                        if (holder.itemView.rootView.isEnabled)
                            expandDogImage(resource, holder.itemView.rootView)
                    }
                    return false
                }

            })
            .into(dogImageView)
    }

    override fun getItemCount() = dogImages.size
}

class DrawableAlwaysCrossFadeFactory : TransitionFactory<Drawable> {
    private val resourceTransition: DrawableCrossFadeTransition = DrawableCrossFadeTransition(
        300,
        true
    ) //customize to your own needs or apply a builder pattern

    override fun build(dataSource: DataSource?, isFirstResource: Boolean): Transition<Drawable> {
        return resourceTransition
    }
}

class DogItemViewHolder(val dogItem: View) :
    RecyclerView.ViewHolder(dogItem)