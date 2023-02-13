namespace csharp_ternary_tests
{
    public class UnitTestsTask2
    {
        [SetUp]
        public void Setup()
        {
        }
        [Test]
        public void Number0()
        {
            Task2 Task2 = new Task2();
            Assert.IsTrue(Task2.ConvertFromCustomTernary("I") == "0");
        }

        [Test]
        public void Number1()
        {
            Task2 Task2 = new Task2();
            Assert.IsTrue(Task2.ConvertFromCustomTernary("A").Equals("1"));
        }

        [Test]
        public void NumberBig()
        {
            Task2 Task2 = new Task2();
            Assert.IsTrue(Task2.ConvertFromCustomTernary("IIAIIIVIAIAIVIIVAVAVAAIAVVIVVIIIVA").Equals("9328476293845623"));
        }

        [Test]
        public void Number11()
        {
            Task2 Task2 = new Task2();
            Assert.IsTrue(Task2.ConvertFromCustomTernary("VIA").Equals("11"));
        }
    }
}