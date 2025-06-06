package com.nhm.distributor.utils

import android.text.TextUtils
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.Collections
import kotlin.math.floor


class EnglishNumberToWords {

    companion object{
        private val tensNames = arrayOf(
            "", " ten", " twenty", " thirty", " forty",
            " fifty", " sixty", " seventy", " eighty", " ninety"
        )

        private val numNames = arrayOf(
            "", " one", " two", " three", " four", " five",
            " six", " seven", " eight", " nine", " ten", " eleven", " twelve", " thirteen",
            " fourteen", " fifteen", " sixteen", " seventeen", " eighteen", " nineteen"
        )


        private fun convertLessThanOneThousand(numberA: Int): String {
            var number = numberA
            var soFar: String
            if (number % 100 < 20) {
                soFar = numNames[number % 100]
                number /= 100
            } else {
                soFar = numNames[number % 10]
                number /= 10
                soFar = tensNames[number % 10] + soFar
                number /= 10
            }
            return if (number == 0) soFar else numNames[number] + " hundred" + soFar
        }

        fun convert(number: Long): String {
            // 0 to 999 999 999 999
            if (number == 0L) {
                return "zero"
            }
            var snumber = number.toString()

            // pad with "0"
            val mask = "000000000000"
            val df = DecimalFormat(mask)
            snumber = df.format(number)

            // XXXnnnnnnnnn
            val billions = snumber.substring(0, 3).toInt()
            // nnnXXXnnnnnn
            val millions = snumber.substring(3, 6).toInt()
            // nnnnnnXXXnnn
            val hundredThousands = snumber.substring(6, 9).toInt()
            // nnnnnnnnnXXX
            val thousands = snumber.substring(9, 12).toInt()
            val tradBillions: String
            tradBillions = when (billions) {
                0 -> ""
                1 -> convertLessThanOneThousand(billions) + " crore "
                else -> convertLessThanOneThousand(billions) + " crore "
            }
            var result = tradBillions
            val tradMillions: String
            tradMillions = when (millions) {
                0 -> ""
                1 -> convertLessThanOneThousand(millions) + " lac "
                else -> convertLessThanOneThousand(millions) + " lac "
            }
            result = result + tradMillions
            val tradHundredThousands: String
            tradHundredThousands = when (hundredThousands) {
                0 -> ""
                1 -> "one thousand "
                else -> convertLessThanOneThousand(hundredThousands) + " thousand "
            }
            result = result + tradHundredThousands
            val tradThousand: String
            tradThousand = convertLessThanOneThousand(thousands)
            result = result + tradThousand

            // remove extra spaces!
            return result.replace("^\\s+".toRegex(), "").replace("\\b\\s{2,}\\b".toRegex(), " ")
        }


        fun convertToIndianCurrency(num: String?): String {
            val bd = BigDecimal(num)
            var number: Long = bd.toLong()
            var no: Long = bd.toLong()
            val decimal = (bd.remainder(BigDecimal.ONE).toDouble() * 100)
            val digits_length = no.toString().length
            var i = 0
            var finalResult = ""
            val str = ArrayList<String?>()
            val words = HashMap<Int, String>()
            words[0] = ""
            words[1] = "One"
            words[2] = "Two"
            words[3] = "Three"
            words[4] = "Four"
            words[5] = "Five"
            words[6] = "Six"
            words[7] = "Seven"
            words[8] = "Eight"
            words[9] = "Nine"
            words[10] = "Ten"
            words[11] = "Eleven"
            words[12] = "Twelve"
            words[13] = "Thirteen"
            words[14] = "Fourteen"
            words[15] = "Fifteen"
            words[16] = "Sixteen"
            words[17] = "Seventeen"
            words[18] = "Eighteen"
            words[19] = "Nineteen"
            words[20] = "Twenty"
            words[30] = "Thirty"
            words[40] = "Forty"
            words[50] = "Fifty"
            words[60] = "Sixty"
            words[70] = "Seventy"
            words[80] = "Eighty"
            words[90] = "Ninety"
            val digits = arrayOf(
                "",
                "Hundred",
                "Thousand",
                "Lakh",
                "Crore",
                "Arab",
                "Kharab",
                "Nil",
                "Padma",
                "Shankh"
            )
            while (i < digits_length) {
                val divider = if (i == 2) 10 else 100
                number = no % divider
                no = no / divider
                i += if (divider == 10) 1 else 2
                if (number > 0) {
                    val counter = str.size
                    val plural = if (counter > 0 && number > 9) "s" else ""
                    val tmp = if (number < 21) words[number.toInt()] + " " +
                            digits[counter] + plural else (words[floor((number / 10).toDouble())
                        .toInt() * 10]
                            + " " + words[(number % 10).toInt()]
                            + " " + digits[counter] + plural)
                    str.add(tmp)
                } else {
                    str.add("")
                }
            }
            Collections.reverse(str)
            var Rupees: String? = null
            Rupees = TextUtils.join(" ", str).trim { it <= ' ' }
            var paise = if (decimal > 0) " And " + words[(decimal - decimal % 10) as Int] + " " +
                    words[(decimal % 10) as Int] else ""
            // AND FORTNY NINE PAISA
            if (!paise.isEmpty()) {
                paise = "$paise Paise"
            }
            finalResult = "$Rupees"
            return finalResult.replace("  ", " ")
                .replace("   ", " ")
        }


        fun convertToIndianCurrency2(num: Double): String {
            val bd = num
            var number: Long = bd.toLong()
            var no: Long = bd.toLong()
            val decimal = (bd * 100)
            val digits_length = no.toString().length
            var i = 0
            var finalResult = ""
            val str = ArrayList<String?>()
            val words = HashMap<Int, String>()
            words[0] = ""
            words[1] = "One"
            words[2] = "Two"
            words[3] = "Three"
            words[4] = "Four"
            words[5] = "Five"
            words[6] = "Six"
            words[7] = "Seven"
            words[8] = "Eight"
            words[9] = "Nine"
            words[10] = "Ten"
            words[11] = "Eleven"
            words[12] = "Twelve"
            words[13] = "Thirteen"
            words[14] = "Fourteen"
            words[15] = "Fifteen"
            words[16] = "Sixteen"
            words[17] = "Seventeen"
            words[18] = "Eighteen"
            words[19] = "Nineteen"
            words[20] = "Twenty"
            words[30] = "Thirty"
            words[40] = "Forty"
            words[50] = "Fifty"
            words[60] = "Sixty"
            words[70] = "Seventy"
            words[80] = "Eighty"
            words[90] = "Ninety"
            val digits = arrayOf(
                "",
                "Hundred",
                "Thousand",
                "Lakh",
                "Crore",
                "Arab",
                "Kharab",
                "Nil",
                "Padma",
                "Shankh"
            )
            while (i < digits_length) {
                val divider = if (i == 2) 10 else 100
                number = no % divider
                no = no / divider
                i += if (divider == 10) 1 else 2
                if (number > 0) {
                    val counter = str.size
                    val plural = if (counter > 0 && number > 9) "s" else ""
                    val tmp = if (number < 21) words[number.toInt()] + " " +
                            digits[counter] + plural else (words[floor((number / 10).toDouble())
                        .toInt() * 10]
                            + " " + words[(number % 10).toInt()]
                            + " " + digits[counter] + plural)
                    str.add(tmp)
                } else {
                    str.add("")
                }
            }
            Collections.reverse(str)
            var Rupees: String? = null
            Rupees = TextUtils.join(" ", str).trim { it <= ' ' }
//            var paise = if (decimal > 0) " And " + words[(decimal - decimal % 10).toInt()] + " " +
//                    words[(decimal % 10).toInt()]
//                    else ""

           val dddd = (decimal - decimal % 10)
//            Log.e("TAG", "ddddXX "+dddd)

            // AND FORTNY NINE PAISA
//            if (!paise.isEmpty()) {
//                paise = "$paise Paise"
//            }
//            finalResult = "$Rupees $paise"
            finalResult = "$Rupees "
            return finalResult.replace("  ", " ")
                .replace("   ", " ")
        }


    }


}