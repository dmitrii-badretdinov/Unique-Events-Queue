using Microsoft.AspNetCore.Mvc;
using System.Globalization;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace server.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ConvertController : ControllerBase
    {
        /// <summary>
        /// Spells out the number in dollars and cents.
        /// The integral part is between 0 and 1 000 000 000.
        /// The cents are between 0 and 99 included.
        /// The separator of thousands is ' ', the separator of cents is ','.
        /// 
        /// The class handles all inner exceptions.
        /// </summary>
        /// <param name="text">The text representation of the number in a form '923 123 456,78'.</param>
        /// <returns>A string that contains a spelled out number.</returns>
        [HttpGet("{text}")]
        public string Get(string text)
        {
            int[] parsedNumber;
            try
            {
                parsedNumber = ParseNumber(text);
            }
            catch (FormatException)
            {
                return String.Format("Unable to convert '{0}'.", text);
            }
            catch (OverflowException e)
            {
                return e.Message;
            }

            try
            {
                return SpellOutNumber(parsedNumber);
            }
            catch (Exception e)
            {
                return String.Format("Internal error. {0}", e.Message);
            }
        }

        /// <summary>
        /// Converts the text number into its integral and decimal part.
        /// The dollar parsing is done separately because Int32.Parse()
        /// does not recognize "NumberStyles.AllowThousands" for some reason.
        /// Can be researched in the future.
        /// The cent parsing can be better too, probably.
        /// </summary>
        /// <param name="text">The whole input number.</param>
        /// <returns></returns>
        /// <exception cref="FormatException">Better thell the user immediately that there's a negatie amount of money.</exception>
        /// <exception cref="OverflowException">Checking what was given as specifications.</exception>
        static private int[] ParseNumber (string text)
        {
            var style = NumberStyles.AllowDecimalPoint | NumberStyles.AllowThousands;
            var culture = new CultureInfo("fr-FR");
            decimal number = Decimal.Parse(text, style, culture);

            if (number < 0)
            {
                throw new FormatException(String.Format("Expected a positive amount of money. Got '{0}'.", number));
            }

            int dollars = Convert.ToInt32(Math.Floor(number));

            if (dollars > 999999999)
            {
                throw new OverflowException(String.Format("Error. Expected dollars below 1 000 000 000. Got '{0}'.", dollars));
            }

            string[] separatedText = text.Split(',');
            int cents = 0;

            if (separatedText.Length > 1)
            {
                if (separatedText[1].Length == 1)
                {
                    cents = Convert.ToInt32(separatedText[1]);
                    cents *= 10;
                }
                else if (separatedText.Length > 1)
                {
                    cents = Convert.ToInt32(separatedText[1].Substring(0,2));
                }
            }

            if (cents > 99)
            {
                throw new OverflowException(String.Format("Error. Expected cents below 99. Got '{0}'.", cents));
            }

            return new int[2] { dollars, cents };
        }

        /// <summary>
        /// The overal philosophy when constructing a number is to make the process modular.
        /// The SpellOutNumber function is responsible for the orchestration of making
        /// the currency suffixes with the help of GetSuffix(), using the spelling core 
        /// SpellOutNumberBelowThousand() to get the bulk of the spelling, and then
        /// handle the edge cases, such as when there is only '0' given or there are no cents.
        /// 
        /// Finally, it composes the parts into the desired answer.
        /// 
        /// Regarding the spaces, the approach is the trailing spaces.
        /// In other words, the part of the number that is being worked on is responsible
        /// for making trailing spaces after itself when necessary and possible.
        /// </summary>
        /// <param name="parsedNumber">Dollars and cents as integers.</param>
        /// <returns>A string that contains the whole spelled out number based on parsedNumber.</returns>
        /// <exception cref="Exception">
        /// Just in case, the size of the input array is checked to be 2 
        /// because there should be no other cases even when the class is developed further.
        /// </exception>
        static private string SpellOutNumber(int[] parsedNumber)
        {
            if (parsedNumber.Length != 2)
            {
                throw new Exception(String.Format("Error. Expected array length 2, got length {0}.", parsedNumber.Length));
            }

            string[] currencyWithSuffixes = new string[2] { "", "" };
            string[] numbersSpelling = new string[2] { "", "" };

            for(int i = 0; i < 2; i++)
            {
                currencyWithSuffixes[i] = currencyLexicalRoots[i] + GetSuffix(parsedNumber[i]);
            }

            if (parsedNumber[0] != 0)
            {
                int dollarsToSpell = parsedNumber[0];
                string[] dollarsSpellingStepThousand = new string[powersOfTenLexicalRoots.Length];

                for (int i = 0; i < powersOfTenLexicalRoots.Length; i++)
                {
                    string partSpelled = SpellOutNumberBelowThousand(dollarsToSpell % 1000);
                    if (!String.IsNullOrEmpty(partSpelled))
                    {
                        dollarsSpellingStepThousand[i] = SpellOutNumberBelowThousand(dollarsToSpell % 1000) + ' ';
                        if (!String.IsNullOrEmpty(powersOfTenLexicalRoots[i]))
                        {
                            dollarsSpellingStepThousand[i] += powersOfTenLexicalRoots[i] + ' ';
                        }
                    }
                    dollarsToSpell /= 1000;
                }
                Array.Reverse(dollarsSpellingStepThousand);
                numbersSpelling[0] = String.Join("", dollarsSpellingStepThousand);
            }
            else
            {
                numbersSpelling[0] = "zero ";
            }

            int centsToSpell = parsedNumber[1];

            if (centsToSpell != 0)
            {
                numbersSpelling[1] = SpellOutNumberBelowThousand(centsToSpell);
            }

            string dollarsPart = numbersSpelling[0] + currencyWithSuffixes[0];
            string result = dollarsPart;

            if (!String.IsNullOrEmpty(numbersSpelling[1]))
            {
                result += " and " + numbersSpelling[1] + ' ' + currencyWithSuffixes[1]; ;
            }

            return result;
        }
        
        /// <summary>
        /// Decides if the currency needs a suffix "s" or not.
        /// </summary>
        /// <param name="number">The amount of currency.</param>
        /// <returns>A suffix for the given ammount.</returns>
        static private string GetSuffix(int number)
        {
            if (number % 10 != 1 || (number / 10) % 10 == 1)
            {
                return "s";
            }
            return "";
        }
        
        /// <summary>
        /// The idea behind this function is that the overall number is a collection of buckets. 
        /// Each bucket holds three digits of the number, or fewer digits if there are not enough.
        /// For instance, the cent bucket will always hold two or fewer digits.
        /// 
        /// The method first deals with the hundreds, then checks if what is left falls into
        /// the special spelling of "eleven, twelve, ... , nineteen".
        /// After that, it handles the tens and finally the last digit.
        /// 
        /// The implementation is a bit jumpy with many returns, but I found no other
        /// maintainable and easily understood way to deconstruct the bucket.
        /// </summary>
        /// <param name="number">The amount of currency in a bucket.</param>
        /// <returns>A spelled out bucket of three digits.</returns>
        /// <exception cref="Exception">
        /// All entries are assumed to be three-digits or fewer, 
        /// so if the number is above that, something is wrong.
        /// </exception>
        static private string SpellOutNumberBelowThousand(int number)
        {
            if (number > 999)
            {
                throw new Exception(String.Format("Expected a number below 1000. Got '{0}'.", number));
            }

            string hundredLexicalRoot = "hundred";
            string result = "";

            if (number / 100 > 0)
            {
                int hundreds = number / 100;
                result += numbersSpellingFromZeroToTen[hundreds] + ' ' + hundredLexicalRoot;
                number %= 100;

                if (number != 0)
                {
                    result += ' ';
                }
            }

            if (number > 10 && number < 20)
            {
                result += numbersSpellingFromElevenToNineteen[number - 11];
                return result;
            }

            if (number > 19)
            {
                int tens = number / 10;
                result += numbersSpellingFromTwentyToNinetyStepTen[tens - 2];
                number %= 10;

                if (number != 0)
                {
                    result += '-' + numbersSpellingFromZeroToTen[number];
                }

                return result;
            }

            if (number != 0)
            {
                result += numbersSpellingFromZeroToTen[number];
            }

            return result;
        }

        private static readonly string[] numbersSpellingFromZeroToTen = new string[11] { 
            "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten" };
        private static readonly string[] numbersSpellingFromElevenToNineteen = new string[9] { 
            "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen" };
        private static readonly string[] numbersSpellingFromTwentyToNinetyStepTen = new string[8] {
            "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety" };
        private static readonly string[] powersOfTenLexicalRoots = new string[3] { "", "thousand", "million" };
        private static readonly string[] currencyLexicalRoots = new string[2] { "dollar", "cent" };
    }
}
