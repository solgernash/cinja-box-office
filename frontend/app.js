const movies = [
  {
    title: "Shadow Circuit",
    genre: "Action",
    rating: "PG-13",
    status: "Currently Running",
    runtime: "2h 04m",
    showtimes: ["2:00 PM", "5:00 PM", "8:00 PM"],
    description: "A rogue projectionist uncovers a secret clan hiding clues inside blockbuster trailers.",
    poster: "https://images.unsplash.com/photo-1524985069026-dd778a71c7b4?auto=format&fit=crop&w=900&q=80",
    trailer: "https://www.youtube.com/embed/dQw4w9WgXcQ"
  },
  {
    title: "Midnight Dojo",
    genre: "Thriller",
    rating: "R",
    status: "Currently Running",
    runtime: "1h 51m",
    showtimes: ["1:30 PM", "6:10 PM", "9:20 PM"],
    description: "Every seat hides a secret when five strangers enter a theater after closing time.",
    poster: "https://images.unsplash.com/photo-1505686994434-e3cc5abf1330?auto=format&fit=crop&w=900&q=80",
    trailer: "https://www.youtube.com/embed/dQw4w9WgXcQ"
  },
  {
    title: "Neon Shuriken",
    genre: "Sci-Fi",
    rating: "PG-13",
    status: "Currently Running",
    runtime: "2h 15m",
    showtimes: ["12:45 PM", "4:30 PM", "10:00 PM"],
    description: "Cyber ninjas race through a glowing city to stop an AI from rewriting every movie ending.",
    poster: "https://images.unsplash.com/photo-1519608487953-e999c86e7455?auto=format&fit=crop&w=900&q=80",
    trailer: "https://www.youtube.com/embed/dQw4w9WgXcQ"
  },
  {
    title: "Popcorn Ronin",
    genre: "Comedy",
    rating: "PG",
    status: "Currently Running",
    runtime: "1h 42m",
    showtimes: ["11:00 AM", "3:15 PM", "7:45 PM"],
    description: "A snack-bar worker must defend the last bucket of legendary buttered popcorn.",
    poster: "https://images.unsplash.com/photo-1512149673953-1e251807ec7c?auto=format&fit=crop&w=900&q=80",
    trailer: "https://www.youtube.com/embed/dQw4w9WgXcQ"
  },
  {
    title: "The Last Ticket",
    genre: "Drama",
    rating: "PG-13",
    status: "Coming Soon",
    runtime: "2h 01m",
    showtimes: ["Coming Soon"],
    description: "A family reconnects while chasing one sold-out ticket to a once-in-a-lifetime premiere.",
    poster: "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=900&q=80",
    trailer: "https://www.youtube.com/embed/dQw4w9WgXcQ"
  },
  {
    title: "Cherry Blossom Heist",
    genre: "Adventure",
    rating: "PG",
    status: "Coming Soon",
    runtime: "1h 56m",
    showtimes: ["Coming Soon"],
    description: "A retired stunt team reunites for one impossible theater vault robbery under falling blossoms.",
    poster: "https://images.unsplash.com/photo-1524231757912-21f4fe3a7200?auto=format&fit=crop&w=900&q=80",
    trailer: "https://www.youtube.com/embed/dQw4w9WgXcQ"
  }
];

const runningMovies = document.querySelector("#runningMovies");
const comingMovies = document.querySelector("#comingMovies");
const searchInput = document.querySelector("#searchInput");
const genreFilter = document.querySelector("#genreFilter");
const emptyState = document.querySelector("#emptyState");
const movieDialog = document.querySelector("#movieDialog");
const dialogContent = document.querySelector("#dialogContent");
const closeDialog = document.querySelector("#closeDialog");

function loadGenres() {
  const genres = [...new Set(movies.map((movie) => movie.genre))].sort();
  genres.forEach((genre) => {
    const option = document.createElement("option");
    option.value = genre;
    option.textContent = genre;
    genreFilter.appendChild(option);
  });
}

function matchesFilters(movie) {
  const searchTerm = searchInput.value.trim().toLowerCase();
  const selectedGenre = genreFilter.value;
  const titleMatch = movie.title.toLowerCase().includes(searchTerm);
  const genreMatch = selectedGenre === "all" || movie.genre === selectedGenre;
  return titleMatch && genreMatch;
}

function movieCard(movie) {
  const article = document.createElement("article");
  article.className = "movie-card";
  article.style.setProperty("--poster", `url('${movie.poster}')`);
  article.innerHTML = `
    <span class="status-pill">${movie.status}</span>
    <h3>${movie.title}</h3>
    <div class="movie-meta"><span>${movie.genre}</span><span>|</span><span>${movie.rating}</span><span>|</span><span>${movie.runtime}</span></div>
    <p>${movie.description}</p>
    <div class="showtimes">
      ${movie.showtimes.map((time) => `<button type="button" data-title="${movie.title}" data-time="${time}">${time}</button>`).join("")}
    </div>
    <button class="details-button" type="button" data-details="${movie.title}">View Details + Trailer</button>
  `;
  return article;
}

function renderMovies() {
  runningMovies.innerHTML = "";
  comingMovies.innerHTML = "";
  const filtered = movies.filter(matchesFilters);

  filtered.filter((movie) => movie.status === "Currently Running").forEach((movie) => runningMovies.appendChild(movieCard(movie)));
  filtered.filter((movie) => movie.status === "Coming Soon").forEach((movie) => comingMovies.appendChild(movieCard(movie)));

  emptyState.hidden = filtered.length > 0;
}

function openMovieDetails(movie) {
  dialogContent.innerHTML = `
    <div class="dialog-layout">
      <div class="dialog-poster" style="--poster: url('${movie.poster}')"></div>
      <div class="dialog-info">
        <p class="section-kicker">${movie.genre} | ${movie.rating} | ${movie.runtime}</p>
        <h2 id="dialogTitle">${movie.title}</h2>
        <p>${movie.description}</p>
        <div class="showtimes">
          ${movie.showtimes.map((time) => `<button type="button" data-title="${movie.title}" data-time="${time}">${time}</button>`).join("")}
        </div>
        <iframe class="trailer-frame" src="${movie.trailer}" title="${movie.title} trailer" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>
      </div>
    </div>
  `;
  movieDialog.showModal();
}

function handleClick(event) {
  const detailsTitle = event.target.dataset.details;
  const showtimeTitle = event.target.dataset.title;
  const showtime = event.target.dataset.time;

  if (detailsTitle) {
    const movie = movies.find((item) => item.title === detailsTitle);
    openMovieDetails(movie);
  }

  if (showtimeTitle && showtime && showtime !== "Coming Soon") {
    const bookingSection = document.querySelector("#booking");
    bookingSection.querySelector("#booking-title").textContent = `${showtimeTitle} at ${showtime}`;
    bookingSection.scrollIntoView({ behavior: "smooth", block: "center" });
    if (movieDialog.open) movieDialog.close();
  }
}

searchInput.addEventListener("input", renderMovies);
genreFilter.addEventListener("change", renderMovies);
document.addEventListener("click", handleClick);
closeDialog.addEventListener("click", () => movieDialog.close());
movieDialog.addEventListener("click", (event) => {
  if (event.target === movieDialog) movieDialog.close();
});

loadGenres();
renderMovies();
