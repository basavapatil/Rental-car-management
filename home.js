let carAvailable = [];

function fetchingData() {

    fetch("https://myfakeapi.com/api/cars/")
        .then((result) => {

            // console.log(result);

            return result.json();
        })
        .then((val) => {

            // console.log(val.products);

            carAvailable = val.cars.slice(0, 100).map((car) => ({
                id: car.id,
                title: `${car.car} ${car.car_model}`,
                thumbnail: "https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?auto=format&fit=crop&w=800&q=80",
                rating: "N/A",
                price: car.price,
                stock: car.availability ? "Available" : "Unavailable",
                year: car.car_model_year,
                color: car.car_color
            }));

            localStorage.setItem(
                "allcars",
                JSON.stringify(carAvailable)
            );

            displayProduct(carAvailable);
        })
        .catch((error) => {
            console.log("Error fetching car data:", error);
        });
}

fetchingData();


function displayProduct(car) {

    let output = "";

    car.map((val, index) => {

        output += `
            <main>

                <div id="image">
                    <img src="${val.thumbnail}" />
                </div>

                <h3>${val.title} (${val.year})</h3>

                <div id="div">

                    <p>Color: ${val.color}</p>

                    <button>${val.price}</button>

                </div>

                <br>

                <div id="dt">

                    <p>${val.stock}</p>

                    <button onclick="details(${val.id})">
                        Details
                    </button>

                </div>

            </main>
        `;
    });

    document.getElementById("productContainer").innerHTML = output;
}


// Search
document.getElementById("search")
    .addEventListener("input", (e) => {

        let searchTerm = e.target.value.toLowerCase();

        let filteredCar = carAvailable.filter((v) => {

            return v.title
                .toLowerCase()
                .includes(searchTerm);

        });

        displayProduct(filteredCar);
    });


// Details
function details(carId) {

    localStorage.setItem("cid", carId);

    window.location.href = "../Details/Details.html";
}