using System.Reflection;
using server_app.Model.ParserClasses;
using server_app.Model;

namespace server_app
{
    public class SearchEngine
    {
        /// <summary>
        /// Searches all data nodes for a match. Case-insensitive. 
        /// A node is an object of type Building, Group, Lock, or Medium.
        /// Each field has its weight that is added to the node weight if there is a match.
        /// If it's a full match, the weight of the field is multiplied by 10.
        /// The transitive nodes have their own weights and are updated with each match.
        /// 
        /// The search logic is not completely abstract - the class names are considered
        /// to be changed not frequently. Therefore, the class names are explicitly used
        /// during the search.
        /// 
        /// It might be possible to handle the lists in DataClass through iterating over its
        /// reflection, but it is obscure and error-prone, so we explicitly use provided lists.
        /// </summary>
        /// <param name="query">Search query.</param>
        /// <returns>Top 10 of the matches. Returning fields: node type, matching field name, matched field contents, node weight.</returns>
        public static void Search(string query)
        {            
            foreach(PropertyInfo property in typeof(DataClass).GetProperties())
            {
                var some = DataClassDb.Data;
                if (property.GetValue(DataClassDb.Data) is null)
                {
                    throw new NullReferenceException(String.Format("The initial data was parsed incorrectly. {0} is null.", property.ToString()));
                }
            }

            ResetAllWeights();

            // The calls below can be done through a cycle if it is
            // expected that the number of classes will increase in the future.
            UpdateNonTransitiveWeights<BaseClass>(query, DataClassDb.Data.buildings, Weights.Building);
            UpdateNonTransitiveWeights<BaseClass>(query, DataClassDb.Data.locks, Weights.Lock);
            UpdateNonTransitiveWeights<BaseClass>(query, DataClassDb.Data.groups, Weights.Group);
            UpdateNonTransitiveWeights<BaseClass>(query, DataClassDb.Data.media, Weights.Medium);
            
            UpdateTransitiveWeights<BaseClass, BaseClass>(query, DataClassDb.Data.buildings, DataClassDb.Data.locks, Weights.LockTransitive, "buildingId");
            UpdateTransitiveWeights<BaseClass, BaseClass>(query, DataClassDb.Data.groups, DataClassDb.Data.media, Weights.MediumTransitive, "groupId");
            
        }

        /// <summary>
        /// Updates the non-transitive weights of nodes.
        /// A node is an object of type Building, Group, Lock, or Medium.
        /// 
        /// The generic <T> style of the implementation is a leftover from the time when there were 4 data classes instead of one.
        /// It gives the function additional flexibility for future extensions of the program.
        /// 
        /// For every item
        ///   for every property of it
        ///     if it contains a property of interest
        ///       get the property's value, compare it with the query, and update the weight.
        /// </summary>
        /// <typeparam name="T">Building, Lock, Group, Medium</typeparam>
        /// <param name="list">List of nodes</param>
        /// <param name="weights">Weights of the fields in a node. Equal for all nodes of the list.</param>
        /// <exception cref="NullReferenceException">Raised if any of the expected properties or values are not there.</exception>
        private static void UpdateNonTransitiveWeights<T> (string query, List<T> list, Dictionary<string, int> weights)
        {
            PropertyInfo[] properties = typeof(T).GetProperties();
            foreach (T item in list)
            {
                foreach (PropertyInfo property in properties)
                {
                    if (weights.ContainsKey(property.Name))
                    {
                        string field = EmptyIfNull(property.GetValue(item)?.ToString().ToLower());
                        if (field.Contains(query))
                        {
                            ThrowIfNull(typeof(T).GetProperty(weightPropertyName), typeof(T), weightPropertyName);
                            int weight = weights[property.Name];
                            if(query.Equals(field))
                            {
                                weight *= 10;
                            }
                            typeof(T).GetProperty(weightPropertyName)?.SetValue(item, (int) typeof(T).GetProperty(weightPropertyName)?.GetValue(item) + weight);
                        }
                    }
                }
            }
        }

        /// <summary>
        /// Updates the transitive weights of nodes.
        /// A node is an object of type Building, Group, Lock, or Medium.
        /// 
        /// Implemented it as a separate function because it adds modularity in case more foreign keys added
        /// at the cost of repeating most of what UpdateNonTransitiveWeights does.
        /// 
        /// The generic <T> style of the implementation is a leftover from the time when there were 4 data classes instead of one.
        /// It gives the function additional flexibility for future extensions of the program.
        /// 
        /// Also, combining these two functions would make the code even more of a nightmare to understand than it already is.
        /// 
        /// For every item in donor
        ///  for every property of it
        ///    if it contains a property of interest
        ///      if there is a match with query
        ///        for each acceptor
        ///          if its foreignKey is equal to donor id
        ///            update its weight
        /// </summary>
        /// <typeparam name="T1">Donor type.</typeparam>
        /// <typeparam name="T2">Acceptor type.</typeparam>
        /// <param name="query">Search query, brought to lowercase.</param>
        /// <param name="donorList">List of donor nodes.</param>
        /// <param name="acceptorList">List of acceptor nodes.</param>
        /// <param name="weights">Weights for the fields of initerest in donor.</param>
        /// <param name="donorKeyPropertyInAcceptor">The name of the donor key in acceptor.</param>
        /// <exception cref="NullReferenceException">Raised if any of the expected properties or values are not there.</exception>
        /// <exception cref="ArgumentException">Raised if donorKey was not found in the acceptor class.</exception>
        private static void UpdateTransitiveWeights<T1, T2>(string query, List<T1> donorList, List<T2> acceptorList, Dictionary<string, int> weights, string donorKeyPropertyInAcceptor)
        {
            PropertyInfo[] donorProperties = typeof(T1).GetProperties();
            PropertyInfo[] acceptorProperties = typeof(T2).GetProperties();
            int foreignKeyEntries = acceptorProperties.Aggregate(0, (total, next) => next.Name.Equals(donorKeyPropertyInAcceptor) ? total + 1 : total);

            if (foreignKeyEntries < 1)
            {
                throw new ArgumentException(String.Format("Donor key was not found in acceptor."));
            }

            foreach (T1 donorItem in donorList)
            {
                foreach (PropertyInfo donorProperty in donorProperties)
                {
                    if (weights.ContainsKey(donorProperty.Name))
                    {
                        string donorField = EmptyIfNull(donorProperty.GetValue(donorItem)?.ToString().ToLower());
                        if (donorField.Contains(query))
                        {
                            int weight = weights[donorProperty.Name];
                            if (query.Equals(donorField))
                            {
                                weight *= 10;
                            }
                            foreach (T2 acceptorItem in acceptorList)
                            {
                                string donorId = typeof(T1).GetProperty(idPropertyName)?.GetValue(donorItem)?.ToString();
                                ThrowIfNull(donorId, typeof(T1), idPropertyName);

                                string foreignKey = typeof(T2).GetProperty(donorKeyPropertyInAcceptor)?.GetValue(acceptorItem)?.ToString();
                                ThrowIfNull(foreignKey, typeof(T2), donorKeyPropertyInAcceptor);

                                if (donorId == foreignKey)
                                {
                                    ThrowIfNull(typeof(T2).GetProperty(weightPropertyName), typeof(T2), weightPropertyName);
                                    typeof(T2).GetProperty(weightPropertyName)?.SetValue(acceptorItem, 
                                        (int)typeof(T2).GetProperty(weightPropertyName)?.GetValue(acceptorItem) + weight);
                                }
                            }
                        }
                    }
                }
            }
        }

        private static void ResetAllWeights()
        {
            ResetListWeights(DataClassDb.Data.buildings);
            ResetListWeights(DataClassDb.Data.locks);
            ResetListWeights(DataClassDb.Data.groups);
            ResetListWeights(DataClassDb.Data.media);
        }
        
        private static void ResetListWeights<T>(List<T> list)
        {
            foreach (T item in list)
            {
                typeof(T).GetProperty(weightPropertyName)?.SetValue(item, 0);
            }
        }

        private static void ThrowIfNull(object obj, Type T, string name)
        {
            if (obj is null)
            {
                throw new NullReferenceException(String.Format("Type {0} doesn't have a property {1}. Expected that it has it.", T, name));
            }
        }

        private static string EmptyIfNull(string input)
        {
            if (!string.IsNullOrEmpty(input))
            {
                return input;
            }
            else
            {
                return string.Empty;
            }
        }

        private static readonly string idPropertyName = "id";
        private static readonly string weightPropertyName = "weight";
    }
}
