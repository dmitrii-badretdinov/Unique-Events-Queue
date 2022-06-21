namespace server_app.Model.ParserClasses
{
    public class BaseClass
    {
        public Guid? id { get; set; }
        public Guid? buildingId { get; set; }
        public Guid? groupId { get; set; }
        public string? type { get; set; }
        public string? shortCut { get; set; }
        public string? name { get; set; }
        public string? description { get; set; }
        public string? serialNumber { get; set; }
        public string? floor { get; set; }
        public string? roomNumber { get; set; }
        public string? owner { get; set; }
        public int? weight { get; set; } = 0;
    }
}
