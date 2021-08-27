# FakeBluetoothPrinter
This is a plugin for my point of sale integration program, 3POS.

It is essentially an emulator of a receipt printer, specifically Star Micronics TSP100 and TSP600 series bluetooth printers. It receives print jobs and draws the content into a buffered image **(scroll down for examples)**. Once the print job is complete, it calls handlers registered by other 3POS plugins and provides access to the image as well as text extracted from the image through Tesseract OCR. This allows other plugins to read the raw text data from the virtual reciept and parse it into a standard format that can be sent directly to POS integration software.

The general data flow can be illustrated like so:

![receipt printer design](https://github.com/AndrewSumsion/3POS/blob/master/doc/receipt-printer-design.png?raw=true)

While somewhat inelegant, this approach is low-impact to adopt at an existing restaurant that only uses service-provided tablets for online ordering services. It allows the restaurant to keep using the tablets for fine-grained control over the orders, and it requires no hacking of the online ordering service's tablets or internal web APIs. All that is necessary to integrate this with the existing setup is to add a computer running this plugin as a printer on a tablet, and optionally enable automatic printing for a completely hands-free experience.

## Example Virtual Receipts
These receipts are direct outputs of this program. They are created from data sent by a real Doordash tablet to a laptop emulating a printer. These images have been used to test the whole pipeline of 3POS, including the OCR and parsing. (Names and Order IDs redacted)

![all-images](https://github.com/AndrewSumsion/FakeBluetoothPrinter/blob/master/images/all-receipts.png?raw=true)

## Important Note
In order for your PC to be recognized as a printer, it must be using a bluetooth adapter whose MAC address begins with any of these strings of 3 bytes:
- 00:12:F3
- 00:15:0E
- 8C:DE:52
- 34:81:F4
- 00:11:62

This is a limitation imposed by the StarPRNT SDK, but it is easily bypassed. You must make sure your bluetooth adapter can reprogram its MAC address, because many built-in adapters can't. If it can't, adapters that can be reprogrammed can be found for as little as $5. Look for one that says "CSR 4.0". To change the MAC address, follow the instructions [here](http://blog.petrilopia.net/hacking/change-your-bluetooth-device-mac-address/).
