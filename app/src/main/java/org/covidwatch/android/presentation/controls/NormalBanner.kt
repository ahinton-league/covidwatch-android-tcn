package org.covidwatch.android.presentation.controls

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import life.league.genesis.R
import life.league.genesis.databinding.WidgetNormalBannerBinding
import life.league.genesis.extension.getDimensionPixelSizeFromAttr
import life.league.genesis.extension.layoutInflator
import life.league.genesis.extension.loadContent
import life.league.genesis.extension.setTextAndVisibility
import life.league.genesis.widget.model.SpacingAttrRes


@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class NormalBanner : FrameLayout {

    private lateinit var binding: WidgetNormalBannerBinding

    constructor(context: Context) : super(context) {
        setupViews(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupViews(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        setupViews(context, attrs)
    }

    private fun setupViews(context: Context, attrSet: AttributeSet? = null) {
        binding = WidgetNormalBannerBinding.inflate(layoutInflator, this, true)

        binding.layout.clipToOutline = true
        binding.backgroundImage.clipToOutline = true
    }

    @JvmOverloads
    @TextProp
    fun setDescriptionText(text: CharSequence = "") {
        binding.description.setTextAndVisibility(text)
    }

    @JvmOverloads
    @TextProp
    fun setActionText(text: CharSequence = "") {
        binding.action.setTextAndVisibility(text)
    }

    @JvmOverloads
    @TextProp
    fun setTitleText(text: CharSequence = "") {
        binding.title.setTextAndVisibility(text)
    }

    @JvmOverloads
    @CallbackProp
    fun setOnClick(clickListener: OnClickListener? = null) {
        binding.root.setOnClickListener(clickListener)
    }

    @JvmOverloads
    @ModelProp(group = "image", options = [ModelProp.Option.IgnoreRequireHashCode])
    fun setBackgroundImageSrc(image: Drawable? = null) {
        if (image != null) {
            binding.backgroundImage.setImageDrawable(image)
        } else {
            binding.backgroundImage.setImageResource(R.drawable.background_banner_noimage)
        }
    }

    @ModelProp(group = "image")
    fun setBackgroundImageResource(@DrawableRes imageResId: Int) {
        binding.backgroundImage.setImageResource(imageResId)
    }

    @JvmOverloads
    @ModelProp
    fun setMarginRes(spacingAttrRes: SpacingAttrRes = SpacingAttrRes()) {
        updateLayoutParams<MarginLayoutParams> {
            updateMargins(
                    top = context.getDimensionPixelSizeFromAttr(spacingAttrRes.topSpacingResId),
                    bottom = context.getDimensionPixelSizeFromAttr(spacingAttrRes.bottomSpacingResId),
                    left = context.getDimensionPixelSizeFromAttr(spacingAttrRes.leftSpacingResId),
                    right = context.getDimensionPixelSizeFromAttr(spacingAttrRes.rightSpacingResId))
        }
    }
}