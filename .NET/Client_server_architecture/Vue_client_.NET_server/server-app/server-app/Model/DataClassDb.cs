using Microsoft.EntityFrameworkCore;
using server_app.Model.ParserClasses;
using System.Text.Json;


namespace server_app.Model
{
    public class DataClassDb : DbContext
    {
        public DataClassDb(DbContextOptions<DataClassDb> options)
       : base(options) 
        {
            if (Data is null)
                Data = JsonSerializer.Deserialize<DataClass>(ReadFile("./Model/sv_lsm_data.json"));
        }

        private string ReadFile(string path)
        {
            using (StreamReader sr = new StreamReader(path))
            {
                return sr.ReadToEnd();
            }
        }

        //public DbSet<DataClass> Data { get; set; }
        
        public static DataClass Data { get; set; }
    }
}
