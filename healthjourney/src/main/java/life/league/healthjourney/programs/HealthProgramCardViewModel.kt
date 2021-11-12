package life.league.healthjourney.programs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import life.league.genesis.widget.card.Card
import life.league.genesis.widget.model.SpacingAttrRes
import life.league.healthjourney.R
import life.league.healthjourney.databinding.ItemHealthProgramCardBinding

@EpoxyModelClass
abstract class HealthProgramCardViewModel :
    EpoxyModelWithHolder<HealthProgramCardViewModel.SimpleDataBindingHolder>() {
    override fun createNewHolder(parent: ViewParent) = SimpleDataBindingHolder()

    override fun getDefaultLayout() = R.layout.item_health_program_card

    override fun buildView(parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemHealthProgramCardBinding.inflate(layoutInflater, parent, false)
        binding.root.tag = binding
        return binding.root
    }

    class SimpleDataBindingHolder : EpoxyHolder() {
        var dataBinding: ItemHealthProgramCardBinding? = null
            private set

        @Suppress("UNCHECKED_CAST")
        override fun bindView(itemView: View) {
            // If the cast is invalid, throw an exception
            dataBinding = itemView.tag as ItemHealthProgramCardBinding
        }
    }

    @EpoxyAttribute
    var overline: String = ""

    @EpoxyAttribute
    var title: String = ""

    @EpoxyAttribute
    var caption: String = ""

    @EpoxyAttribute
    var description: String = ""

    @EpoxyAttribute
    var imageContentId: String = ""

    @EpoxyAttribute
    var imageUrl: String = ""

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var clickListener: View.OnClickListener? = null

    @EpoxyAttribute
    var applyMargins: Boolean = false

    @EpoxyAttribute
    var titleMaxLines: Int? = null

    @EpoxyAttribute
    var imageShape: Card.ImageShape = Card.ImageShape.LANDSCAPE

    override fun bind(holder: SimpleDataBindingHolder) {
        holder.dataBinding?.also { binding ->
            binding.card.setCaptionText(caption)
            binding.card.setOverlineText(overline)
            binding.card.setTitleText(title)
            binding.card.setDescriptionText(description)
            binding.card.setOnClickListener(clickListener)
            binding.card.setImageContentId(imageContentId)
            binding.card.setImageUrl(imageUrl)
            binding.card.setImageShape(imageShape)
            binding.card.setTitleMaxLines(titleMaxLines)
            if (applyMargins) {
                binding.card.setMarginRes(
                    SpacingAttrRes(
                        leftSpacingResId = R.attr.spacing_one_and_half,
                        rightSpacingResId = R.attr.spacing_one_and_half,
                        topSpacingResId = R.attr.spacing_two
                    )
                )
            }
        }
    }
}