package lu.esklepios.app.util

/**
 * Formats a Caisse Nationale de Santé (Luxembourg national health insurance)
 * number for display. The first 9 digits identify the holder and are safe to
 * show; subsequent digits are masked.
 */
object CnsFormatter {
    /**
     * Returns `cns` unchanged if it has 9 characters or fewer, otherwise
     * `"<first 9> ••••"`.
     */
    fun mask(cns: String): String = if (cns.length > VISIBLE_PREFIX_LENGTH) "${cns.take(VISIBLE_PREFIX_LENGTH)} ••••" else cns

    private const val VISIBLE_PREFIX_LENGTH = 9
}
