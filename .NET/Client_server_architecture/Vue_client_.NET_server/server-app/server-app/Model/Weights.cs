namespace server_app.Model
{
    public static class Weights
    {
        public static readonly Dictionary<string, int> Building = new Dictionary<string, int>()
        {
            { "id", 0 },
            { "shortCut", 7 },
            { "name", 9 },
            { "description", 5 }
        };

        public static readonly Dictionary<string, int> Lock = new Dictionary<string, int>()
        {
            { "id", 0 },
            { "buildingId", 0 },
            { "type", 3 },
            { "name", 10 },
            { "serialNumber", 8 },
            { "floor", 6 },
            { "roomNumber", 6 },
            { "description", 6 }
        };

        public static readonly Dictionary<string, int> LockTransitive = new Dictionary<string, int>()
        {
            { "name", 8 },
            { "shortCut", 5 }
        };

        public static readonly Dictionary<string, int> Group = new Dictionary<string, int>()
        {
            { "name", 9 },
            { "description", 5 }
        };

        public static readonly Dictionary<string, int> Medium = new Dictionary<string, int>()
        {
            { "id", 0 },
            { "groupId", 0 },
            { "type", 3 },
            { "owner", 10 },
            { "serialNumber", 8 },
            { "description", 6 }
        };

        public static readonly Dictionary<string, int> MediumTransitive = new Dictionary<string, int>()
        {
            { "name", 8 }
        };
    }
}
