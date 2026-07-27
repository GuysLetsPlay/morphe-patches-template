package app.template.patches.twitch.playback

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility
import app.morphe.patcher.patch.bytecodePatch

// pvq is the app's PlaybackRateSetting value class in Twitch 29.9.1. Its static list is used by the
// VOD playback-rate picker. Pinning the exact class is safe here because this patch intentionally
// supports that single Twitch version.
private object PlaybackRateListFingerprint : Fingerprint(
    name = "<clinit>",
    returnType = "V",
    custom = { _, classDef -> classDef.type == "Lpvq;" },
)

@Suppress("unused")
val fineGrainedVodPlaybackSpeedPatch = bytecodePatch(
    name = "Fine-grained VOD playback speeds",
    description = "Adds VOD playback speeds from 0.50x through 2.00x in 0.05x increments.",
) {
    compatibleWith(
        Compatibility(
            name = "Twitch",
            packageName = "tv.twitch.android.app",
            appIconColor = 0x9147FF,
            targets = listOf(AppTarget("29.9.1")),
        ),
    )

    execute {
        // The stock list contains 0.50x--2.00x in 0.25x steps. Build the replacement list directly
        // instead of altering the app's obfuscated Kotlin sequence lambdas, which keeps this patch
        // limited to the VOD settings value source.
        val rates = (10..40).map { it / 20f }
        val rateInstructions = rates.joinToString("\n") { rate ->
            val bits = rate.toRawBits().toUInt().toString(16).padStart(8, '0')

            """
                const v1, 0x$bits
                new-instance v2, Lpvq;
                invoke-direct {v2, v1}, Lpvq;-><init>(F)V
                invoke-virtual {v0, v2}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z
            """.trimIndent()
        }

        PlaybackRateListFingerprint.method.addInstructions(
            0,
            """
                new-instance v0, Ljava/util/ArrayList;
                const/16 v1, 0x1f
                invoke-direct {v0, v1}, Ljava/util/ArrayList;-><init>(I)V
                $rateInstructions
                sput-object v0, Lpvq;->b:Ljava/util/List;
                return-void
            """.trimIndent(),
        )
    }
}
