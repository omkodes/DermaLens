package com.example.dermalens.ml

import com.example.dermalens.data.local.entity.ScreeningHistoryEntity
import com.example.dermalens.data.local.entity.UserProfileEntity

object RiskScorer {
    const val MAX_POINTS = 19f  // ABCDE: 3+4+2+2 = 11 pts; Profile: 3+4+1 = 8 pts

    fun calculate(
        familyHistory: Boolean,
        previousSkinCancer: Boolean,
        hadBadSunburns: Boolean,
        lesionHasChanged: Boolean,
        lesionBleeding: Boolean,
        lesionMultipleColors: Boolean,
        lesionLargerThan6mm: Boolean
    ): Float {
        var pts = 0
        // Profile risk (max 8 pts)
        if (familyHistory) pts += 3
        if (previousSkinCancer) pts += 4
        if (hadBadSunburns) pts += 1

        // ABCDE checklist risk (max 11 pts)
        if (lesionHasChanged) pts += 3
        if (lesionBleeding) pts += 4
        if (lesionMultipleColors) pts += 2
        if (lesionLargerThan6mm) pts += 2

        return (pts / MAX_POINTS).coerceIn(0f, 1f)
    }

    fun calculate(profile: UserProfileEntity, entity: ScreeningHistoryEntity): Float {
        return calculate(
            familyHistory = profile.familyHistory,
            previousSkinCancer = profile.previousSkinCancer,
            hadBadSunburns = profile.hadBadSunburns,
            lesionHasChanged = entity.lesionHasChanged,
            lesionBleeding = entity.lesionBleeding,
            lesionMultipleColors = entity.lesionMultipleColors,
            lesionLargerThan6mm = entity.lesionLargerThan6mm
        )
    }
}
