package life.league.healthjourney.journey.models

import androidx.annotation.StringRes
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class HealthActivitiesCategory(
        val header: HealthActivitiesCategoryHeader,
        val activities: List<HealthJourneyItem>
) : Serializable

@JsonClass(generateAdapter = true)
data class HealthActivitiesCategoryHeader(
        @StringRes val titleResId: Int,
        @StringRes val descriptionResId: Int? = null,
        val status: String = ""
) : Serializable