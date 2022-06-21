using Microsoft.EntityFrameworkCore;

namespace server_app.Model.ParserClasses
{
    [Keyless]
    public class DataClass
    {
        public List<BaseClass>? buildings { get; set; }

        public List<BaseClass>? locks { get; set; }

        public List<BaseClass>? groups { get; set; }

        public List<BaseClass>? media { get; set; }

    }
}
