package app.template.patches.twitch.ads

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

object DisplayAdResponseParserFingerprint : Fingerprint(
    classFingerprint = Fingerprint(
        strings = listOf(
            "failed to parse display ad response: ",
            "could not parse content type: ",
        ),
    ),
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    name = "a",
    returnType = "Lnq;",
    parameters = listOf("Lretrofit2/adapter/rxjava2/Result;", "Z"),
)
