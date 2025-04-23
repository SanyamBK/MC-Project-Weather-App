package mc.project.weatherapp

import mc.project.weatherapp.api.SongSuggestion

object MusicDatabase {
    private val weatherToSongs = mapOf(
        "Rain" to listOf(
            SongSuggestion("Tip Tip Barsa Paani", "Alka Yagnik & Udit Narayan", "https://www.youtube.com/watch?v=gj9OL7fvhGc"),
            SongSuggestion("Barso Re Megha", "Shreya Ghoshal", "https://www.youtube.com/watch?v=Pr-jfBmZjzI"),
            SongSuggestion("Rim Jhim Gire Sawan", "Kishore Kumar", "https://www.youtube.com/watch?v=b89hXuRx3dg"),
            SongSuggestion("Bheegi Bheegi", "James", "https://www.youtube.com/watch?v=WeY9hdsmIaQ"),
            SongSuggestion("Sawan Mein Lag Gayi Aag", "Mika Singh", "https://www.youtube.com/watch?v=g5Q3yBuP_38"),
            SongSuggestion("Barsaat Ki Dhun", "Jubin Nautiyal", "https://www.youtube.com/watch?v=JxXNJKJiUyg")
        ),

        "Sunny" to listOf(
            SongSuggestion("Ilahi", "Arijit Singh", "https://www.youtube.com/watch?v=fdubeMFwuGs"),
            SongSuggestion("Badtameez Dil", "Benny Dayal", "https://www.youtube.com/watch?v=e6cgsagaMRY"),
            SongSuggestion("Sunny Sunny", "Yo Yo Honey Singh", "https://www.youtube.com/watch?v=3154VyTIdqE"),
            SongSuggestion("Galti Se Mistake", "Arijit Singh", "https://www.youtube.com/watch?v=ROtv13CqjzU"),
            SongSuggestion("Sham", "Amit Trivedi & Nikhil D'Souza", "https://www.youtube.com/watch?v=4Q46xYqUwZQ"),
            SongSuggestion("Baliye Re", "Neeraj Shridhar", "https://www.youtube.com/watch?v=txInOsPt62E")
        ),

        "Cloudy" to listOf(
            SongSuggestion("Kabira", "Arijit Singh", "https://www.youtube.com/watch?v=09SlNCQQ8kc"),
            SongSuggestion("Tum Hi Ho", "Arijit Singh", "https://www.youtube.com/watch?v=VXARHLXj8a4"),
            SongSuggestion("Tujhe Bhula Diya", "Mohit Chauhan", "https://www.youtube.com/watch?v=SiU5dsCM5YM"),
            SongSuggestion("Phir Le Aya Dil", "Arijit Singh", "https://www.youtube.com/watch?v=6S8tc7M3X2E"),
            SongSuggestion("Raabta", "Arijit Singh", "https://www.youtube.com/watch?v=z_7tZkH3x8w"),
            SongSuggestion("Agar Tum Saath Ho", "Alka Yagnik", "https://www.youtube.com/watch?v=6FURuLYrR_Q")
        ),

        "Storm" to listOf(
            SongSuggestion("Zinda", "Siddharth Basrur", "https://www.youtube.com/watch?v=4q9UafsiQ6k"),
            SongSuggestion("Dhoom Again", "Vishal Dadlani", "https://www.youtube.com/watch?v=Y50t3hL0Z6A"),
            SongSuggestion("Malang", "Vishal Dadlani", "https://www.youtube.com/watch?v=9G1QH7zZ_hw"),
            SongSuggestion("Dhoom Machale", "Sunidhi Chauhan", "https://www.youtube.com/watch?v=4gLld7vKRB8"),
            SongSuggestion("Chak Lein De", "Vishal Dadlani", "https://www.youtube.com/watch?v=0rFZ8XpzZ2w"),
            SongSuggestion("Bhaag DK Bose", "Ram Sampath", "https://www.youtube.com/watch?v=3VoT5tFqG7Y")
        ),

        "Winter" to listOf(
            SongSuggestion("Sawan Aaya Hai", "Arko Pravo Mukherjee", "https://www.youtube.com/watch?v=6M3y89wg7U4"),
            SongSuggestion("Chaiyya Chaiyya", "Sukhwinder Singh", "https://www.youtube.com/watch?v=kC5k6sRqeeM"),
            SongSuggestion("Ishq Wala Love", "Neeti Mohan", "https://www.youtube.com/watch?v=3x1CL6qwTdA"),
            SongSuggestion("Tum Se Hi", "Mohit Chauhan", "https://www.youtube.com/watch?v=3jD4uQO0K-Y"),
            SongSuggestion("Dil Ibadat", "KK", "https://www.youtube.com/watch?v=Xh4U0124-YA"),
            SongSuggestion("Tere Sang Yaara", "Atif Aslam", "https://www.youtube.com/watch?v=1H9hB4u0Kq4")
        ),

        "General" to listOf(
            SongSuggestion("Gerua", "Arijit Singh", "https://www.youtube.com/watch?v=6S1y4ZqnDdQ"),
            SongSuggestion("Kal Ho Naa Ho", "Sonu Nigam", "https://www.youtube.com/watch?v=9Oqg7dJg0X4"),
            SongSuggestion("Ae Dil Hai Mushkil", "Arijit Singh", "https://www.youtube.com/watch?v=5-1nS6ZqdBE"),
            SongSuggestion("Channa Mereya", "Arijit Singh", "https://www.youtube.com/watch?v=9J5bqKfazQU"),
            SongSuggestion("Tera Ban Jaunga", "Akhil Sachdeva", "https://www.youtube.com/watch?v=7A7NX2dK-gE"),
            SongSuggestion("Pehla Nasha", "Udit Narayan", "https://www.youtube.com/watch?v=3B1H8hDZ_7I")
        )
    )

    fun getSongs(weatherCondition: String): List<SongSuggestion> {
        return weatherToSongs[weatherCondition] ?: emptyList()
    }
}
