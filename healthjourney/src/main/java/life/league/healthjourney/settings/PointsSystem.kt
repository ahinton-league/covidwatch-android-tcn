package life.league.healthjourney.settings

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

sealed class PointsSystem {

    abstract val canEarnPoints: Boolean
    abstract val eligibility: PointsEligibility

    enum class PointsEligibility(val text: String) {
        ELIGIBLE("eligible"), INELIGIBLE("ineligible"), NOT_DETERMINED("not_determined")
    }

    sealed class PCH: PointsSystem() {

        object Undetermined: PCH() {
            override val canEarnPoints: Boolean = false
            override val eligibility: PointsEligibility = PointsEligibility.NOT_DETERMINED
        }

        object NonPcOptimum: PCH() {
            override val canEarnPoints: Boolean = false
            override val eligibility: PointsEligibility = PointsEligibility.INELIGIBLE
        }

        class PcOptimum(val optimumTokens: OptimumTokens): PCH() {
            override val canEarnPoints: Boolean = true
            override val eligibility: PointsEligibility = PointsEligibility.ELIGIBLE
        }

    }

    object League : PointsSystem() {
        override val canEarnPoints: Boolean = true
        override val eligibility: PointsEligibility = PointsEligibility.ELIGIBLE
    }

    object Fusion : PointsSystem() {
        override val canEarnPoints: Boolean = true
        override val eligibility: PointsEligibility = PointsEligibility.ELIGIBLE
    }

}

@JsonClass(generateAdapter = true)
data class OptimumTokens(
    @Json(name = "member_id") val memberId: String,
    @Json(name = "device_fingerprint") val deviceFingerprint: String,
    val authorization: String
)
