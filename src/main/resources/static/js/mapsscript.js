console.log("this is mapsscript")

let map, infoWindow;

async function initmap() {

	

	const { Map } = await google.maps.importLibrary("maps");

	const { AdvancedMarkerElement, PinElement} = await google.maps.importLibrary("marker");

	map = new Map(document.getElementById("map"), {

		zoom: 4,
		center: { lat: 17.7044941, lng: 83.1756083 },
		mapId: "",

	});

	infoWindow = new google.maps.InfoWindow();

	const locationButton = document.getElementById("mylocation");
	

	console.log(locationButton.textContent)

	locationButton.classList.add("custom-map-control-button");
	//map.controls[google.maps.ControlPosition.TOP_CENTER].push(locationButton);
	locationButton.addEventListener("click", () => {
		// Try HTML5 geolocation.
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(
				(position) => {
					const pos = {
						lat: position.coords.latitude,
							lng: position.coords.longitude,
					};

					const icon = document.createElement("div");

					icon.innerHTML = '<i class="fa-solid fa-location-dot"></i>';

					const faPin = new PinElement({
						glyph: icon,
						glyphColor: "#ff8300",
						background: "#0000FF",
						borderColor: "#ff8300",
					});
					const faMarker = new AdvancedMarkerElement({
						map,
						position: pos,
						content: faPin.element,
						title: "A marker using a FontAwesome icon for the glyph.",
					});
					
					
                 console.log(pos) 
					map.setCenter(pos);
					map.setZoom(14);
					findPlaces(pos)
				},
				() => {
					handleLocationError(true, infoWindow, map.getCenter());
				},
				
				
			);
		} else {
			// Browser doesn't support Geolocation
			handleLocationError(false, infoWindow, map.getCenter());
		}
		
		
	});
	
	
}

function handleLocationError(browserHasGeolocation, infoWindow, pos) {
	infoWindow.setPosition(pos);
	infoWindow.setContent(
		browserHasGeolocation
			? "Error: The Geolocation service failed."
			: "Error: Your browser doesn't support geolocation.",
	);
	infoWindow.open(map);
}

async function findPlaces(pos) {
	
	const legalplaces = document.getElementById("places")
	console.log(legalplaces.textContent)
	
	console.log(pos)
  const { Place } = await google.maps.importLibrary("places");
  const { AdvancedMarkerElement } = await google.maps.importLibrary("marker");
  const request = {
    textQuery: legalplaces.textContent,
    fields: ["displayName", "location", "businessStatus","addressComponents"],
    locationBias: pos,
    language: "en-US",
    
    
   
    
  };
  //@ts-ignore
  const { places } = await Place.searchByText(request);

  if (places.length) {
    console.log(places);

    const { LatLngBounds } = await google.maps.importLibrary("core");
    const bounds = new LatLngBounds();

    // Loop through and get all the results.
    places.forEach((place) => {
      const markerView = new AdvancedMarkerElement({
        map,
        position: place.location,
        title: place.displayName,
         gmpClickable: true,
      });

      bounds.extend(place.location);
      console.log(place.displayName);
     console.log(place.addressComponents)
      
   
      
    });
    map.fitBounds(bounds);
  } else {
    console.log("No results");
  }
}



window.initmap = initmap();
