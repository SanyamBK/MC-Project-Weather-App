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
            SongSuggestion("Sham", "Amit Trivedi & Nikhil D'Souza", "https://youtu.be/fFyXcX-s0C8?si=Wf8XFhW8W1mDy9Rx"),
            SongSuggestion("Baliye Re", "Neeraj Shridhar", "https://www.youtube.com/watch?v=txInOsPt62E")

        ),

        "Cloudy" to listOf(
            SongSuggestion("Kabira", "Arijit Singh", "https://youtu.be/6UPZo8PX9OI?si=qL71jRTpBwIwzp90"),
            SongSuggestion("Tum Hi Ho", "Arijit Singh", "https://www.youtube.com/watch?v=Umqb9KENgmk"),
            SongSuggestion("Tujhe Bhula Diya", "Mohit Chauhan", "https://www.youtube.com/watch?v=SiU5dsCM5YM"),
            SongSuggestion("Phir Le Aya Dil", "Arijit Singh", "https://youtu.be/R4YeD7aoOmU?si=HEmMNHqqvsiUM2Ep"),
            SongSuggestion("Raabta", "Arijit Singh", "https://youtu.be/iYy9kr45d1o?si=R3AMa4ks_1QiEKfB"),
            SongSuggestion("Agar Tum Saath Ho", "Alka Yagnik & Arijit Singh", "https://youtu.be/sK7riqg2mr4?si=wJeR4zkUUOav8dYw")

        ),

        "Storm" to listOf(
            SongSuggestion("Zinda", "Siddharth Mahadevan", "https://youtu.be/Ax0G_P2dSBw?si=TD25iN8ARZn1HxkU"),
            SongSuggestion("Dhoom Again", "Vishal Dadlani", "https://www.youtube.com/watch?v=lP89_U3MBlU"),
            SongSuggestion("Malang", "Siddharth Mahadevan & Shilpa Rao", "https://youtu.be/SxoTAvwCr4A?si=AwbvKtPgTwCvpNLR"),
            SongSuggestion("Dhoom Machale", "Sunidhi Chauhan", "https://youtu.be/2uUmHTgT65I?si=nYwJAs5xsR3qTFN1"),
            SongSuggestion("Chak Lein De", "Vishal Dadlani", "https://youtu.be/kd-6aw99DpA?si=da44ClpgdqL4RI4g"),
            SongSuggestion("Bhaag DK Bose", "Ram Sampath", "https://youtu.be/vs1IDdap3X4?si=R-EX0Nkmxm5jyZ9z")

        ),

        "Winter" to listOf(
            SongSuggestion("Sawan Aaya Hai", "Arko Pravo Mukherjee", "https://www.youtube.com/watch?v=Gh9b0OPdDDI"),
            SongSuggestion("Chaiyya Chaiyya", "Sukhwinder Singh", "https://www.youtube.com/watch?v=9MX-QejdVaQ"),
            SongSuggestion("Ishq Wala Love", "Neeti Mohan", "https://www.youtube.com/watch?v=wV-o_KugibY"),
            SongSuggestion("Tum Se Hi", "Mohit Chauhan", "https://www.youtube.com/watch?v=I94fhjQ-U30"),
            SongSuggestion("Dil Ibadat", "KK", "https://m.youtube.com/watch?v=pMRqHk_0Bw0&pp=ygUSI2RpbGliYWRhdGtycnJoYWhp"),
            SongSuggestion("Tere Sang Yaara", "Atif Aslam", "https://www.youtube.com/watch?v=XT9sSinTvpk")
        ),

        "General" to listOf(
            SongSuggestion("Gerua", "Arijit Singh & Antara Mitra", "https://www.youtube.com/watch?v=2zoIA42nJJc&pp=0gcJCfcAhR29_xXO"),
            SongSuggestion("Kal Ho Naa Ho", "Sonu Nigam", "https://www.youtube.com/watch?v=r_oBWnz7wLE"),
            SongSuggestion("Ae Dil Hai Mushkil", "Arijit Singh", "https://youtu.be/6FURuLYrR_Q?si=owVZ7N4M5XlRCGAU"),
            SongSuggestion("Channa Mereya", "Arijit Singh", "https://www.youtube.com/watch?v=_BCk3o4NxYQ"),
            SongSuggestion("Tera Ban Jaunga", "Akhil Sachdeva & Tulsi Kumar", "https://youtu.be/Qdz5n1Xe5Qo?si=wald5ZpBeJVqc_9u"),
            SongSuggestion("Pehla Nasha", "Udit Narayan & Sadhana Sargam", "https://m.youtube.com/watch?v=hRlGYdWInAU&pp=ygULI3BlaGxha2h1bWE%3D")
        )


    )


    fun getSongs(weatherCondition: String): List<SongSuggestion> {
        return weatherToSongs[weatherCondition] ?: emptyList()
    }
}