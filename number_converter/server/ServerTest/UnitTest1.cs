namespace ServerTest
{
    /// <summary>
    /// I used unit tests because they were very handy in debugging the logic.
    /// </summary>
    [TestClass]
    public class UnitTest1
    {
        [TestMethod]
        public void Dollars0()
        {
            server.Controllers.ConvertController controller = new();
            Assert.AreEqual("zero dollars", controller.Get("0"));
        }

        [TestMethod]
        public void Dollars1()
        {
            server.Controllers.ConvertController controller = new();
            Assert.AreEqual("one dollar", controller.Get("1"));
        }

        [TestMethod]
        public void Dollars2()
        {
            server.Controllers.ConvertController controller = new();
            Assert.AreEqual("two dollars", controller.Get("2"));
        }

        [TestMethod]
        public void Dollars25_1()
        {
            server.Controllers.ConvertController controller = new();
            Assert.AreEqual("twenty-five dollars and ten cents", controller.Get("25,1"));
        }
        
        [TestMethod]
        public void Dollars0_01()
        {
            server.Controllers.ConvertController controller = new();
            Assert.AreEqual("zero dollars and one cent", controller.Get("0,01"));
        }

        [TestMethod]
        public void Dollars0_99()
        {
            server.Controllers.ConvertController controller = new();
            Assert.AreEqual("zero dollars and ninety-nine cents", controller.Get("0,99"));
        }

        [TestMethod]
        public void Dollars45100()
        {
            server.Controllers.ConvertController controller = new();
            Assert.AreEqual("forty-five thousand one hundred dollars", controller.Get("45 100"));
        }

        [TestMethod]
        public void Dollars999999999_99()
        {
            server.Controllers.ConvertController controller = new();
            Assert.AreEqual("nine hundred ninety-nine million nine hundred ninety-nine thousand nine hundred ninety-nine dollars and ninety-nine cents", 
                controller.Get("999 999 999,99"));
        }

        [TestMethod]
        public void Dollars239845723_1()
        {
            server.Controllers.ConvertController controller = new();
            Assert.AreEqual("two hundred thirty-nine million eight hundred forty-five thousand seven hundred twenty-three dollars and one cent",
                controller.Get("239 845 723,1"));
        }
    }
}