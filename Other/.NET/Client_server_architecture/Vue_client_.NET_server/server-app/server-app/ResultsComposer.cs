using server_app.Model.ParserClasses;
using System.Reflection;

namespace server_app
{
    public static class ResultsComposer
    {
        /// <summary>
        /// Returns a specified number of nodes that have most weight.
        /// 
        /// The memory and computing expenditure can be vastly improved with the following:
        /// 1. Changing the algorithm to keep track of only the necessary number of best suggestions.
        /// 2. Caching the previously-completed requests.
        /// </summary>
        /// <param name="data">A data structure with nodes whose weights were updated.</param>
        /// <param name="howMany">A number of how many suggestions to return.</param>
        /// <returns>A list of suggestions.</returns>
        public static List<BaseClass> ComposeResults(DataClass data, int howMany)
        {
            List<BaseClass> listOfAll = new List<BaseClass>(
                data.buildings.Count + 
                data.locks.Count + 
                data.media.Count + 
                data.groups.Count
                ); 
            listOfAll.AddRange(data.buildings);
            listOfAll.AddRange(data.locks);
            listOfAll.AddRange(data.media);
            listOfAll.AddRange(data.groups);

            listOfAll.Sort((x, y) => x.weight.Value.CompareTo(y.weight.Value));

            List<BaseClass> result = listOfAll.GetRange(listOfAll.Count - howMany, howMany);

            result.Reverse();

            return result;
        }
    }
}
