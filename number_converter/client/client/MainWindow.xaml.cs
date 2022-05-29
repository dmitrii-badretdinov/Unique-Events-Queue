using System.Net.Http;
using System.Windows;

namespace client
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
            tbInput.KeyUp += TbInput_KeyUp;

        }

        private void TbInput_KeyUp(object sender, System.Windows.Input.KeyEventArgs e)
        {
            if(e.Key == System.Windows.Input.Key.Enter)
            {
                Button_Click(sender, e);
            }
        }

        private async void Button_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                string response = await _client.GetStringAsync("http://localhost:5192/api/convert/" + tbInput.Text);
                tbOutput.Text = response;
            }
            catch (System.Exception ex)
            {
                tbOutput.Text = ex.Message;
            }
        }

        private static readonly HttpClient _client = new HttpClient();
    }
}
