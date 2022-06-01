namespace csharp_ternary_tests
{
    public class UnitTestsTask1
    {
        [SetUp]
        public void Setup()
        {
        }

        [Test]
        public void Number0()
        {
            Task1 task1 = new Task1();
            Assert.IsTrue(task1.convertToCustomTernary("0") == "I");
        }

        [Test]
        public void Number1()
        {
            Task1 task1 = new Task1();
            Assert.IsTrue(task1.convertToCustomTernary("1").Equals("A"));
        }

        [Test]
        public void NumberBig()
        {
            Task1 task1 = new Task1();
            Assert.IsTrue(task1.convertToCustomTernary("9328476293845623").Equals("IIAIIIVIAIAIVIIVAVAVAAIAVVIVVIIIVA"));
        }

        [Test]
        public void Number11()
        {
            Task1 task1 = new Task1();
            Assert.IsTrue(task1.convertToCustomTernary("11").Equals("VIA"));
        }
    }
}