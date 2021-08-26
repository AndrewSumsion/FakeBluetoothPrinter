# FakeBluetoothPrinter
This is a plugin for my point of sale integration program, 3POS.

It is essentially an emulator of a receipt printer, specifically Star Micronics TSP100 and TSP600 series bluetooth printers. It receives print jobs and draws the content into a buffered image. Once the print job is complete, it calls handlers registered by other 3POS plugins and provides access to the image as well as text extracted from the image through Tesseract OCR. This allows other plugins to read the raw text data from the virtual reciept and parse it into a standard format that can be sent directly to POS integration software.

The general data flow can be illustrated like so:
![receipt printer design](https://github.com/AndrewSumsion/3POS/blob/master/doc/receipt-printer-design.png?raw=true)

While somewhat inelegant, this approach is low-impact to adopt at an existing restaurant that only uses service-provided tablets for online ordering services. It allows the restaurant to keep using the tablets for fine-grained control over the orders, and it requires no hacking of the online ordering service's tablets or internal web APIs. All that is necessary to integrate this with the existing setup is to add a computer running this plugin as a printer on a tablet, and optionally enable automatic printing for a completely hands-free experience.
