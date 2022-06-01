using System;
using System.Collections.Generic;
using System.Text;
using System.Numerics;

namespace csharp_ternary
{
    public class Task2
    {
        public Task2()
        {
            _customFromTernary = new Dictionary<char, int> {
            { 'I', 0 },
            { 'A', 1 },
            { 'V', 2 }
            };
            StringBuilder sb = new StringBuilder();
            foreach (char key in _customFromTernary.Keys)
            {
                sb.Append(key);
            }
            _allowedCharacters = sb.ToString();
        }

        public string ConvertFromCustomTernary(string input)
        {
            _resultingNumber = 0;
            _powersOfTernaryInDecimal = new BigInteger[input.Length];
            for (int i = 0; i < _powersOfTernaryInDecimal.Length; i++)
            {
                _powersOfTernaryInDecimal[i] = (BigInteger)Math.Pow(3, i);
            }
            CheckInputValidity(input);
            for (int i = 0; i < input.Length; i++)
            {
                _resultingNumber += _customFromTernary[input[i]] * _powersOfTernaryInDecimal[i];
            }
            return _resultingNumber.ToString();
        }

        void CheckInputValidity(string input)
        {
            foreach (char c in input)
            {
                if (!_allowedCharacters.Contains(c))
                {
                    throw new Exception("Input contains forbidden characters.");
                }
            }
        }

        readonly Dictionary<char, int> _customFromTernary;
        readonly string _allowedCharacters;
        BigInteger[]? _powersOfTernaryInDecimal;
        BigInteger _resultingNumber;
    }
}
