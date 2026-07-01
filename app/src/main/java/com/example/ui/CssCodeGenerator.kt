package com.example.ui

object CssCodeGenerator {

    private fun hexToRgb(hex: String): Triple<Int, Int, Int> {
        return try {
            val h = hex.removePrefix("#")
            Triple(
                h.substring(0, 2).toInt(16),
                h.substring(2, 4).toInt(16),
                h.substring(4, 6).toInt(16)
            )
        } catch (e: Exception) {
            Triple(255, 255, 255)
        }
    }

    fun generateGlass(
        blur: Float,
        opacity: Float,
        saturation: Float,
        radius: Float,
        colorHex: String,
        darkText: Boolean,
        bgUrl: String
    ): Pair<String, String> {
        val (r, g, b) = hexToRgb(colorHex)
        val alpha = (opacity / 100f).let { String.format("%.2f", it) }
        
        val bgBody = if (bgUrl.isNotEmpty()) {
            "\n\nbody {\n  background-image: url('$bgUrl');\n  background-size: cover;\n}"
        } else {
            ""
        }

        val css = """
/* Glassmorphism */
.glass-card {
  backdrop-filter: blur(${blur.toInt()}px) saturate(${saturation.toInt()}%);
  -webkit-backdrop-filter: blur(${blur.toInt()}px) saturate(${saturation.toInt()}%);
  background-color: rgba($r, $g, $b, $alpha);
  border-radius: ${radius.toInt()}px;
  border: 1px solid rgba($r, $g, $b, 0.2);
}$bgBody
""".trimIndent()

        val html = """
<div class="glass-card">
  <h2>Title</h2>
  <p>Content here.</p>
</div>
""".trimIndent()

        return Pair(css, html)
    }

    fun generateSlider(
        urlsString: String,
        speed: Float,
        radius: Float,
        showArrows: Boolean
    ): Pair<String, String> {
        val urls = urlsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val allUrls = if (urls.isNotEmpty()) urls else listOf(
            "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800",
            "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=800"
        )
        val spd = speed.toInt()
        val br = radius.toInt()

        val css = """
/* Image Slider */
.slider {
  position: relative;
  width: 100%;
  overflow: hidden;
  border-radius: ${br}px;
  aspect-ratio: 16 / 9;
}
.slide {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  opacity: 0;
  transition: opacity 0.6s ease;
  z-index: 0;
}
.slide.active { opacity: 1; z-index: 1; }
.arrow {
  position: absolute;
  top: 50%; transform: translateY(-50%);
  background: rgba(0,0,0,0.4);
  border: none; border-radius: 50%;
  color: #fff; font-size: 20px;
  width: 36px; height: 36px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; z-index: 10;
  transition: background 0.2s;
  backdrop-filter: blur(4px);
}
.arrow:hover { background: rgba(0,0,0,0.8); }
.arrow.prev { left: 12px; }
.arrow.next { right: 12px; }
.dots {
  position: absolute; bottom: 12px;
  left: 50%; transform: translateX(-50%);
  display: flex; gap: 6px; z-index: 10;
}
.dot {
  width: 8px; height: 8px; border-radius: 50%;
  background: rgba(255,255,255,0.4);
  cursor: pointer; transition: 0.2s;
}
.dot.active { background: #fff; transform: scale(1.2); }
""".trimIndent()

        val imgDivs = allUrls.mapIndexed { index, url ->
            "  <div class=\"slide${if (index == 0) " active" else ""}\" style=\"background-image:url('$url')\"></div>"
        }.joinToString("\n")

        val dotDivs = allUrls.mapIndexed { index, _ ->
            "  <div class=\"dot${if (index == 0) " active" else ""}\" onclick=\"goSlide($index)\"></div>"
        }.joinToString("\n")

        val arrowHtml = if (showArrows) {
            "  <button class=\"arrow prev\" onclick=\"sMove(-1)\">&#8249;</button>\n  <button class=\"arrow next\" onclick=\"sMove(1)\">&#8250;</button>\n"
        } else {
            ""
        }

        val html = """
<div class="slider">
$imgDivs
$arrowHtml  <div class="dots">
$dotDivs
  </div>
</div>
<script>
var si=0,slides=document.querySelectorAll('.slide'),dots=document.querySelectorAll('.dot');
fun goSlide(n){slides[si].classList.remove('active');dots[si].classList.remove('active');si=(n+slides.length)%slides.length;slides[si].classList.add('active');dots[si].classList.add('active');}
fun sMove(d){goSlide(si+d);}
setInterval(()=>sMove(1),${spd}000);
</script>
""".trimIndent()

        return Pair(css, html)
    }

    fun generateHeader(
        bgColor: String,
        blur: Float,
        height: Float,
        shadow: Float,
        borderBottom: Boolean,
        brandText: String,
        navLinks: String
    ): Pair<String, String> {
        val (r, g, b) = hexToRgb(bgColor)
        val links = navLinks.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val isLightBg = (r + g + b) > 400
        val linkColor = if (isLightBg) "rgba(0,0,0,0.6)" else "rgba(255,255,255,0.7)"
        val borderCss = if (borderBottom) "  border-bottom: 1px solid rgba(255,255,255,0.1);\n" else ""

        val css = """
/* Sticky Header */
header {
  position: sticky;
  top: 0;
  z-index: 999;
  width: 100%;
  height: ${height.toInt()}px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  background-color: rgba($r, $g, $b, 0.8);
  backdrop-filter: blur(${blur.toInt()}px);
  -webkit-backdrop-filter: blur(${blur.toInt()}px);
  box-shadow: 0 2px ${shadow.toInt()}px rgba(0,0,0,0.1);
$borderCss}
header nav a {
  color: $linkColor;
  text-decoration: none;
  margin-left: 24px;
  font-size: 14px;
  font-weight: 700;
  transition: opacity 0.2s;
}
header nav a:hover { opacity: 0.7; }
""".trimIndent()

        val linkHtmls = links.joinToString("\n") { "    <a href=\"#\">$it</a>" }
        val html = """
<header>
  <div class="logo"><strong>$brandText</strong></div>
  <nav>
$linkHtmls
  </nav>
</header>
""".trimIndent()

        return Pair(css, html)
    }

    fun generateShadow(
        offsetX: Float,
        offsetY: Float,
        blur: Float,
        spread: Float,
        colorHex: String,
        opacity: Float,
        inset: Boolean,
        radius: Float
    ): Pair<String, String> {
        val (r, g, b) = hexToRgb(colorHex)
        val op = String.format("%.2f", opacity / 100f)
        val insetPrefix = if (inset) "inset " else ""
        val shadow = "$insetPrefix${offsetX.toInt()}px ${offsetY.toInt()}px ${blur.toInt()}px ${spread.toInt()}px rgba($r, $g, $b, $op)"

        val css = """
.element {
  box-shadow: $shadow;
  border-radius: ${radius.toInt()}px;
}
""".trimIndent()

        val html = """
<div class="element">Content</div>
""".trimIndent()

        return Pair(css, html)
    }

    fun generateGradient(
        type: String,
        angle: Float,
        color1: String,
        stop1: Float,
        color2: String,
        stop2: Float,
        useColor3: Boolean,
        color3: String,
        stop3: Float
    ): Pair<String, String> {
        val stops = if (useColor3) {
            "$color1 ${stop1.toInt()}%, $color2 ${stop2.toInt()}%, $color3 ${stop3.toInt()}%"
        } else {
            "$color1 ${stop1.toInt()}%, $color2 ${stop2.toInt()}%"
        }

        val grad = when (type) {
            "linear-gradient" -> "linear-gradient(${angle.toInt()}deg, $stops)"
            "radial-gradient" -> "radial-gradient(circle, $stops)"
            else -> "conic-gradient(from ${angle.toInt()}deg, $stops)"
        }

        val css = """
.element {
  background: $grad;
}
""".trimIndent()

        val html = """
<div class="element"></div>
""".trimIndent()

        return Pair(css, html)
    }

    fun generateButton(
        label: String,
        style: String,
        bgColor: String,
        textColor: String,
        radius: Float,
        fontSize: Float,
        paddingH: Float,
        paddingV: Float,
        hoverFx: String
    ): Pair<String, String> {
        var bgStyle = bgColor
        var borderStyle = "none"
        var colorStyle = textColor

        when (style) {
            "outline" -> {
                bgStyle = "transparent"
                borderStyle = "2px solid $bgColor"
                colorStyle = bgColor
            }
            "ghost" -> {
                bgStyle = "transparent"
                colorStyle = bgColor
            }
            "gradient" -> {
                bgStyle = "linear-gradient(135deg, $bgColor, #555555)"
            }
        }

        val hov = when (hoverFx) {
            "scale" -> "\n.btn:hover { transform: scale(1.04); }"
            "shadow" -> "\n.btn:hover { box-shadow: 0 8px 24px rgba(0,0,0,0.15); }"
            "lighten" -> "\n.btn:hover { opacity: 0.85; }"
            else -> ""
        }

        val css = """
.btn {
  background: $bgStyle;
  color: $colorStyle;
  border: $borderStyle;
  border-radius: ${radius.toInt()}px;
  font-size: ${fontSize.toInt()}px;
  padding: ${paddingV.toInt()}px ${paddingH.toInt()}px;
  cursor: pointer;
  font-weight: 800;
  letter-spacing: 0.05em;
  transition: all 0.2s ease;
}$hov
""".trimIndent()

        val html = """
<button class="btn">$label</button>
""".trimIndent()

        return Pair(css, html)
    }

    fun generateTicker(
        itemsText: String,
        speed: Float,
        direction: String,
        bgColor: String,
        textColor: String,
        fontSize: Float,
        gap: Float
    ): Pair<String, String> {
        val items = itemsText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        val doubled = (items + items).joinToString("\n") { "    <span class=\"t-item\">$it</span>" }
        val dir = if (direction == "right") " reverse" else ""

        val css = """
.ticker { overflow: hidden; background: $bgColor; }
.t-track {
  display: flex;
  white-space: nowrap;
  animation: ticker ${speed.toInt()}s linear infinite$dir;
}
.t-item {
  font-size: ${fontSize.toInt()}px;
  font-weight: 700;
  color: $textColor;
  padding: 12px ${gap.toInt()}px;
  flex-shrink: 0;
}
@keyframes ticker {
  from { transform: translateX(0); }
  to { transform: translateX(-50%); }
}
""".trimIndent()

        val html = """
<div class="ticker">
  <div class="t-track">
$doubled
  </div>
</div>
""".trimIndent()

        return Pair(css, html)
    }

    fun generateLoader(
        style: String,
        color: String,
        size: Float,
        speed: Float,
        stroke: Float
    ): Pair<String, String> {
        val spd = String.format("%.1f", speed / 10f)
        val sz = size.toInt()
        val sw = stroke.toInt()

        val (css, html) = when (style) {
            "spinner" -> {
                val c = """
.spinner {
  width: ${sz}px; height: ${sz}px;
  border: ${sw}px solid ${color}33;
  border-top: ${sw}px solid $color;
  border-radius: 50%;
  animation: spin ${spd}s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
""".trimIndent()
                val h = "<div class=\"spinner\"></div>"
                Pair(c, h)
            }
            "dots" -> {
                val d = (sz / 3)
                val gp = (sz / 4)
                val lf = (sz / 2.5).toInt()
                val c = """
.dots { display: flex; gap: ${gp}px; }
.dots div {
  width: ${d}px; height: ${d}px;
  border-radius: 50%; background: $color;
  animation: bounce ${spd}s ease-in-out infinite;
}
.dots div:nth-child(2) { animation-delay: .15s; }
.dots div:nth-child(3) { animation-delay: .3s; }
@keyframes bounce { 0%,100%{transform:translateY(0)} 50%{transform:translateY(-${lf}px)} }
""".trimIndent()
                val h = "<div class=\"dots\"><div></div><div></div><div></div></div>"
                Pair(c, h)
            }
            "pulse" -> {
                val c = """
.pulse {
  width: ${sz}px; height: ${sz}px;
  border-radius: 50%; background: $color;
  animation: pulse ${spd}s ease-in-out infinite;
}
@keyframes pulse { 0%,100%{transform:scale(.8);opacity:.4} 50%{transform:scale(1.2);opacity:1} }
""".trimIndent()
                val h = "<div class=\"pulse\"></div>"
                Pair(c, h)
            }
            else -> {
                val bw = (sz / 5)
                val c = """
.bars { display: flex; gap: 6px; }
.bars div {
  width: ${bw}px; height: ${sz}px;
  border-radius: 4px; background: $color;
  animation: bars ${spd}s ease-in-out infinite;
}
.bars div:nth-child(2){animation-delay:.1s}
.bars div:nth-child(3){animation-delay:.2s}
.bars div:nth-child(4){animation-delay:.3s}
@keyframes bars { 0%,100%{transform:scaleY(.3);opacity:.4} 50%{transform:scaleY(1);opacity:1} }
""".trimIndent()
                val h = "<div class=\"bars\"><div></div><div></div><div></div><div></div></div>"
                Pair(c, h)
            }
        }

        return Pair(css, html)
    }
}
