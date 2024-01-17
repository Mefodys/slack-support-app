fun main() {
    var iceCreamInventory = mutableMapOf('t' to 1, 'u' to 2)
    iceCreamInventory.set('t', 5)
    println(iceCreamInventory['t']) // prints: 5
    println(iceCreamInventory) // prints: {t = 5, u = 2}
    private var x = iceCreamInventory.getOrDefault('t', 0)
    println(x) // prints: 1

}