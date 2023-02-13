using Microsoft.AspNetCore.Mvc;
using server_app.Model;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace server_app.Controllers
{
    /// <summary>
    /// Controller to handle search queries.
    /// Json is read as whole file, which can lead to overflows
    /// if the file is longer than 2^31 characters or 2GB+.
    /// https://stackoverflow.com/a/140749/5859354
    /// 
    /// This concern is not addressed in this version as it is
    /// assumed that the input file is of smaller volume.
    /// </summary>
    [ApiController]
    [Route("[controller]")]
    public class SearchController : ControllerBase
    {
        /// <summary>
        /// The database context is put here for one reason - so its
        /// constructor is called and the json is parsed into the Data property in DataClassDb.
        /// 
        /// Otherwise there was no stable place in the app that was not reinitialized with every query.
        /// Therefore, a database context was used to read once and store the parsed data.
        /// </summary>
        /// <param name="context">An Entity Framework database context.</param>
        public SearchController(DataClassDb context)
        {
        }
        /// <summary>
        /// Usage example: "search/Logist"
        /// 
        /// The search is case-insensitive and doesn't support special symbols such as ?, *, and so on.
        /// It checks if the given string is the substring of any field from the data.
        /// 
        /// Potential improvement - a client can specify how many suggestions to return.
        /// </summary>
        /// <param name="query">Search request based on which the recommendation is given.</param>
        /// <returns>JSON list of relevant suggestions</returns>
        [HttpGet("{query}")]
        [Produces("application/json")]
        public JsonResult Get(string query)
        {
            try
            {
                SearchEngine.Search(query.ToLower());
            }
            catch (Exception e)
            {
                return new JsonResult(e.Message);
            }

            var data = ResultsComposer.ComposeResults(DataClassDb.Data, 10);
            var results = new JsonResult(data);
            return results;
        }

    }
}
