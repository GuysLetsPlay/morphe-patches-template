package app.template.patches.twitch.chat

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility
import app.morphe.patcher.patch.bytecodePatch

@Suppress("unused")
val showDeletedMessagesPatch = bytecodePatch(
    name = "Show deleted messages",
    description = "Lets everyone reveal deleted or moderated chat messages by tapping them.",
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
        val method = DeletedMessageSpanCtorFingerprint.method
        val definingClass = DeletedMessageSpanCtorFingerprint.classDef.type
        method.addInstructions(
            method.implementation!!.instructions.lastIndex,
            """
                const/4 p3, 0x1
                iput-boolean p3, p0, $definingClass->c:Z
            """,
        )
    }
}
