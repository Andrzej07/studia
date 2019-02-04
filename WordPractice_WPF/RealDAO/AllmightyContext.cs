namespace DAO
{
    using System;
    using System.Data.Entity;
    using System.Data.SqlClient;
    using System.ComponentModel.DataAnnotations.Schema;
    using System.Linq;
    using Interfaces;
    public partial class AllmightyContext : DbContext
    {
        
        public AllmightyContext()
            : base("name=AllmightyContext")
        {
            // ConnectionStringBuilder
           // var builder = new SqlConnectionStringBuilder() { DataSource=};

        } /*
        public AllmightyContext()
            : base(@"data source=&quot;C:\Users\Maciej Olszowy\Documents\Visual Studio 2015\Projects\NaukaSlow\RealDAO\NaukaSlowOlszowy.sqlite&quot;")
        {
        }*/

        public DbSet<User> Users { get; set; }
        public DbSet<Word> Words { get; set; }
        public DbSet<WordConnection> WordConnections { get; set; }
        public DbSet<WordUserHistory> WordUserHistories { get; set; }
        public DbSet<LanguageString> Languages { get; set; }

        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {            
        }
    }
}
/*
  <connectionStrings>
    <add name = "AllmightyContext" connectionString="data source=&quot;C:\Users\Maciej Olszowy\Documents\Visual Studio 2015\Projects\NaukaSlow\RealDAO\NaukaSlowOlszowy.sqlite&quot;" providerName="System.Data.SQLite.EF6" />
  </connectionStrings>*/