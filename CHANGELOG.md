# Changelog 

## [1802.2.7] - 2022-11-24

### Fixes

* Fixed another BigInteger->int overflow issue which could cause unexpected results extracting from link blocks, or connecting to one to mods such as AE2 or Refined Storage (causing negative item amounts to be wrongly returned)
* Fixed excessive EMC sync packets being sent to clients when there are a lot of deployed blocks modifying player's personal EMC 
  * Power Flowers in particular, but also any link blocks
* Fixed EMC +/- display inaccuracy; value was 5 times lower than it should have been
  * Display bug only; actual EMC changes were correct
  * Now correctly displays a 5-second moving average of incoming/outgoing EMC

## [1802.2.6] - 2022-11-10

### Fixes

* Fixed BigInteger->long overflow in a couple of places which caused inaccurate EMC displays and prevent items being extracted from EMC link in some situations (thanks @fred21O4)

## [1802.2.5] - 2022-10-31

* This project was formerly known as ProjectEX but has been rebranded to indicate that it is not affiliated with ProjectE in any way
* All the functionality previously in the 1.12.2 release of ProjectEX (and missing from the 1.16.5 release) has been restored in ExtendedeXchange

