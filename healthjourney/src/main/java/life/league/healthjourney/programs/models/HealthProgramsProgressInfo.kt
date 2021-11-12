package life.league.healthjourney.programs.models

import life.league.rewards.model.AchievementDetail
import life.league.rewards.model.MilestoneTrackerResult

data class HealthProgramsProgressInfo(
    val healthPrograms: HealthPrograms,
    val recentAchievements: List<AchievementDetail>,
    val milestoneTrackerResult: MilestoneTrackerResult
)