using Microsoft.EntityFrameworkCore;
using server_app.Model;

var builder = WebApplication.CreateBuilder(args);
builder.Services.AddDbContext<DataClassDb>(opt => opt.UseInMemoryDatabase("DataClassDatabase"));
// A development environment to prevent or at least reduce the issues with Mozilla CORS policies.
builder.Services.AddCors(options =>
{
    options.AddDefaultPolicy(
        policy =>
        {
            policy.AllowAnyOrigin()
                .AllowAnyMethod();
        });
});

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors();

app.UseAuthorization();

app.MapControllers();

app.Run();
