package app.template.patches.purpletv.playback

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility
import app.morphe.patcher.patch.bytecodePatch

// PurpleTV 25.3.0 keeps the VOD-speed menu in this unobfuscated value class. Its original list is
// generated from 0.50 through 2.00 in 0.25 steps.
private object PlaybackRateSettingInitializerFingerprint : Fingerprint(
    name = "<clinit>",
    returnType = "V",
    custom = { _, classDef ->
        classDef.type == "Ltv/twitch/android/shared/preferences/PlaybackRateSetting;"
    },
)

@Suppress("unused")
val fineGrainedPurpleTvVodPlaybackSpeedPatch = bytecodePatch(
    name = "Fine-grained VOD playback speeds",
    description = "Adds PurpleTV VOD playback speeds from 0.50x through 2.00x in 0.05x increments. " +
        "Original PurpleTV 25.3.0 APK: https://purpletv.aeong.win/Beta/2738.apk",
) {
    compatibleWith(
        Compatibility(
            name = "PurpleTV",
            packageName = "tv.orange",
            appIconColor = 0x9147FF,
            targets = listOf(AppTarget("25.3.0")),
        ),
    )

    execute {
        // Keep every float derived directly from an integer divided by 20. This avoids cumulative
        // floating-point additions and therefore keeps labels clean (for example, exactly "1.05x").
        val rates = (10..40).map { it / 20f }
        val rateInstructions = rates.joinToString("\n") { rate ->
            val bits = rate.toRawBits().toUInt().toString(16).padStart(8, '0')

            """
                const v1, 0x$bits
                new-instance v0, Ltv/twitch/android/shared/preferences/PlaybackRateSetting;
                invoke-direct {v0, v1}, Ltv/twitch/android/shared/preferences/PlaybackRateSetting;-><init>(F)V
                sget-object v1, Ltv/twitch/android/shared/preferences/PlaybackRateSetting;->values:Ljava/util/List;
                invoke-interface {v1, v0}, Ljava/util/List;->add(Ljava/lang/Object;)Z
            """.trimIndent()
        }

        PlaybackRateSettingInitializerFingerprint.method.addInstructions(
            0,
            """
                new-instance v0, Ltv/twitch/android/shared/preferences/PlaybackRateSetting${'$'}Companion;
                const/4 v1, 0x0
                invoke-direct {v0, v1}, Ltv/twitch/android/shared/preferences/PlaybackRateSetting${'$'}Companion;-><init>(Lkotlin/jvm/internal/DefaultConstructorMarker;)V
                sput-object v0, Ltv/twitch/android/shared/preferences/PlaybackRateSetting;->Companion:Ltv/twitch/android/shared/preferences/PlaybackRateSetting${'$'}Companion;
                new-instance v0, Ljava/util/ArrayList;
                const/16 v1, 0x1f
                invoke-direct {v0, v1}, Ljava/util/ArrayList;-><init>(I)V
                sput-object v0, Ltv/twitch/android/shared/preferences/PlaybackRateSetting;->values:Ljava/util/List;
                $rateInstructions
                return-void
            """.trimIndent(),
        )
    }
}
