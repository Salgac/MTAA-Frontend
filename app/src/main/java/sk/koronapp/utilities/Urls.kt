package sk.koronapp.utilities

class Urls {
    companion object{
        private const val ROOT = "http://139.162.130.177:8000/api/"

        const val LOGIN = ROOT + "login/"
        const val REGISTER = ROOT + "register/"
        const val USER = ROOT + "user/"
        const val AVATAR = ROOT + "avatar/"
        const val DEMAND = ROOT + "demand/"
    }
}