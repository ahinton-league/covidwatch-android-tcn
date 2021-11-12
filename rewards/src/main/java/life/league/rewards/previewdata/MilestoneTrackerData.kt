package life.league.rewards.previewdata

import life.league.rewards.model.AchievementImage
import life.league.rewards.model.Milestone
import life.league.rewards.model.MilestoneTrackerResult

object MilestoneTrackerData {

    private const val MOCK_IMAGE_URL = "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/160/apple/285/party-popper_1f389.png"

    fun getMilestoneData(): MilestoneTrackerResult {
        val milestone1 = Milestone(count = 1, unit = "week", subtitle = "streak", image = AchievementImage(small = MOCK_IMAGE_URL, medium = MOCK_IMAGE_URL, large = MOCK_IMAGE_URL))
        val milestone2 = Milestone(count = 1, unit = "activity", subtitle = "complete", image = AchievementImage(small = MOCK_IMAGE_URL, medium = MOCK_IMAGE_URL, large = MOCK_IMAGE_URL))

        return MilestoneTrackerResult(milestones = listOf(milestone1, milestone2))
    }
}