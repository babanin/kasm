package katartal.util

fun <T> Class<T>.path(): String {
    val pkg = this.`package`
    return pkg.name.replace(".", "/") + "/" + this.simpleName
}