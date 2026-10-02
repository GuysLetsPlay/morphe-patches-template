package app.template.patches.hvvswitch

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.methodCall
import app.morphe.patcher.string
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.template.patches.shared.Constants.COMPATIBILITY_HVV_SWITCH

private const val LIGHT_HVV_MAP_STYLE =
    "mapbox://styles/hochbahn/cm8d9wt3z00qw01sbhxx9def5"
private const val CUSTOM_HVV_MAP_STYLE =
    "mapbox://styles/hochbahn/cmbgo3l2b008p01sc1kai6ue5"
private const val DARK_MAP_STYLE = "mapbox://styles/mapbox/dark-v11"

@Suppress("unused")
val darkModePatch = bytecodePatch(
    name = "Dark mode",
    description = "Forces the dark interface and switches every HVV map style to Mapbox Dark.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_HVV_SWITCH)

    execute {
        // Set the value passed into the appearance flow to HVV's existing Dark enum.
        val appearanceIndex = AppearanceFlowFingerprint.instructionMatches[1].index
        AppearanceFlowFingerprint.method.addInstructions(
            appearanceIndex,
            "sget-object p1, Ljx0;->a:Ljx0;",
        )

        fun replaceStyle(fingerprint: Fingerprint, match: Int, register: String) {
            val index = fingerprint.instructionMatches[match].index
            fingerprint.method.addInstructions(
                index + 1,
                "const-string $register, \"$DARK_MAP_STYLE\"",
            )
        }

        replaceStyle(MapStyleGn1AFingerprint, 0, "v4")

        // This renderer has two style constants in the same method. Insert from the end
        // so the first insertion does not shift the second fingerprint index.
        replaceStyle(MapStyleGn1InvokeFingerprint, 1, "v12")
        replaceStyle(MapStyleGn1InvokeFingerprint, 0, "v7")

        replaceStyle(MapStyleHepFingerprint, 0, "v14")
        replaceStyle(MapStyleJ7pFingerprint, 0, "v4")
        replaceStyle(MapStyleFhjFingerprint, 0, "v6")
        replaceStyle(MapStyleVf8Fingerprint, 0, "v4")
        replaceStyle(MapStyleIe3Fingerprint, 0, "v13")
    }
}

private val AppearanceFlowFingerprint = Fingerprint(
    definingClass = "Luik;",
    name = "<init>",
    parameters = listOf("Landroid/content/Context;", "Z"),
    filters = listOf(
        string("datastore_theme_preferences"),
        methodCall(definingClass = "Lkn3;", name = "k0"),
    ),
)

private val MapStyleGn1AFingerprint = Fingerprint(
    definingClass = "Lgn1;",
    name = "a",
    parameters = listOf("Ljava/lang/Object;", "Ljava/lang/Object;", "Ljava/lang/Object;"),
    filters = listOf(string(LIGHT_HVV_MAP_STYLE)),
)

private val MapStyleGn1InvokeFingerprint = Fingerprint(
    definingClass = "Lgn1;",
    name = "invoke",
    parameters = listOf("Ljava/lang/Object;", "Ljava/lang/Object;", "Ljava/lang/Object;"),
    filters = listOf(
        string(LIGHT_HVV_MAP_STYLE),
        string(LIGHT_HVV_MAP_STYLE),
    ),
)

private val MapStyleHepFingerprint = Fingerprint(
    definingClass = "Lhep;",
    name = "b",
    parameters = listOf("Lef7;", "Lrqj;", "Lxx3;", "I"),
    filters = listOf(string(LIGHT_HVV_MAP_STYLE)),
)

private val MapStyleJ7pFingerprint = Fingerprint(
    definingClass = "Lj7p;",
    name = "b",
    filters = listOf(string(CUSTOM_HVV_MAP_STYLE)),
)

private val MapStyleFhjFingerprint = Fingerprint(
    definingClass = "Lfhj;",
    name = "c",
    parameters = listOf("Lrgj;", "Lef7;", "Lgf7;", "Lgf7;", "Lgf7;", "Lxx3;", "I"),
    filters = listOf(string(LIGHT_HVV_MAP_STYLE)),
)

private val MapStyleVf8Fingerprint = Fingerprint(
    definingClass = "Lvf8;",
    name = "l",
    parameters = listOf("Lqsi;", "Ljava/lang/String;", "F", "F", "I"),
    filters = listOf(string(LIGHT_HVV_MAP_STYLE)),
)

private val MapStyleIe3Fingerprint = Fingerprint(
    definingClass = "Lie3;",
    name = "invoke",
    parameters = listOf("Ljava/lang/Object;", "Ljava/lang/Object;", "Ljava/lang/Object;"),
    filters = listOf(string(LIGHT_HVV_MAP_STYLE)),
)
