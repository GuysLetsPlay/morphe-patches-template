package app.template.patches.twitch.chat

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

object DeletedMessageSpanCtorFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR),
    returnType = "V",
    parameters = listOf(
        "Ljava/lang/String;",
        "Landroid/text/SpannedString;",
        "Z",
        "Ltv/twitch/android/core/mvp/viewdelegate/EventDispatcher;",
    ),
    custom = { _, classDef ->
        classDef.superclass == "Landroid/text/style/ClickableSpan;"
    },
)

object CommunityPointsStateProviderFingerprint : Fingerprint(
    classFingerprint = Fingerprint(
        strings = listOf("CommunityPointsButtonStateProvider\$State"),
    ),
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    name = "U2",
    returnType = "Lvg8;",
    parameters = listOf("Lxg8;"),
)
