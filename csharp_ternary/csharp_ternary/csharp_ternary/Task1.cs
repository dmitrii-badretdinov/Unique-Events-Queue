using System.Text;
using System.Numerics;

namespace csharp_ternary
{
    public class Task1
    {
        public string convertToCustomTernary(string input)
        {
            _resultingStringBuilder.Clear();
            try
            {
                _inputNumber = BigInteger.Parse(input);
            }
            catch
            {
                Console.WriteLine("The input number is incorrect.");
                throw;
            }
            if (_inputNumber < 0)
            {
                throw new Exception("The number is less than 0.");
            }
            if (_inputNumber == 0)
            {
                _resultingStringBuilder.Append(_customTernary[0]);
            }
            int remainder = 0;

            while (_inputNumber > 0)
            {
                remainder = (int)(_inputNumber % 3);
                _inputNumber /= 3;
                _resultingStringBuilder.Append(_customTernary[remainder]);
            }

            return _resultingStringBuilder.ToString();
        }

        readonly string[] _customTernary = new string[3] { "I", "A", "V" };
        BigInteger _inputNumber;
        StringBuilder _resultingStringBuilder = new StringBuilder();
    }
}
