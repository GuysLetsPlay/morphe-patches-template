package app.template.patches.twitch.ads

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

object LiveManifestUrlBuilderFingerprint : Fingerprint(
    classFingerprint = Fingerprint(
        strings = listOf("usher.ttvnw.net", "fast_bread"),
    ),
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    name = "invoke",
    returnType = "Ljava/lang/Object;",
    parameters = listOf("Ljava/lang/Object;", "Ljava/lang/Object;"),
)
