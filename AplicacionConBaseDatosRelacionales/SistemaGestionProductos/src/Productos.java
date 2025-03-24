import org.w3c.dom.*; // Importa las clases necesarias para manipular documentos XML
import javax.xml.parsers.*; // Importa las clases necesarias para el parseo de XML
import javax.xml.transform.*; // Importa las clases necesarias para transformar XML
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*; // Importa las clases necesarias para manejo de archivos y excepciones
import java.util.Scanner; // Importa Scanner para recibir entrada de usuario

public class Productos {

    public static void main(String[] args) {
        // Crea un objeto Scanner para capturar la entrada del usuario desde la consola
        Scanner scanner = new Scanner(System.in);

        // Bucle infinito para el menú principal que se repetirá hasta que el usuario salga
        while (true) {
            // Muestra las opciones del menú en la consola
            System.out.println("1. Agregar Producto");
            System.out.println("2. Buscar Producto");
            System.out.println("3. Modificar Producto");
            System.out.println("4. Eliminar Producto");
            System.out.println("5. Exportar a CSV");
            System.out.println("6. Salir");
            System.out.print("Elige una opción: ");
            int opcion = scanner.nextInt(); // Captura la opción seleccionada
            scanner.nextLine();  // Consume el salto de línea extra después del entero

            try {
                // Evalúa la opción seleccionada y llama al método correspondiente
                switch (opcion) {
                    case 1 -> agregarProducto(scanner); // Llama a agregarProducto
                    case 2 -> buscarProducto(scanner); // Llama a buscarProducto
                    case 3 -> modificarProducto(scanner); // Llama a modificarProducto
                    case 4 -> eliminarProducto(scanner); // Llama a eliminarProducto
                    case 5 -> exportarACSV(); // Llama a exportarACSV
                    case 6 -> { // Caso para salir del programa
                        System.out.println("Saliendo...");
                        System.exit(0); // Termina la ejecución del programa
                    }
                    default -> System.out.println("Opción no válida."); // Mensaje para opción no válida
                }

            } catch (Exception e) {
                // Captura y muestra errores inesperados que ocurran en el menú
                System.out.println("Error inesperado: " + e.getMessage());
            }
        }
    }

    /**
     * Agrega un nuevo producto al archivo XML.
     */
    static void agregarProducto(Scanner scanner) throws IOException {
        try {
            // Carga el archivo XML o crea uno nuevo si no existe
            Document doc = cargarXML();

            // Solicita al usuario los detalles del producto
            System.out.print("Nombre del producto: ");
            String nombre = scanner.nextLine(); // Captura el nombre del producto
            System.out.print("Precio: ");
            int precio = scanner.nextInt(); // Captura el precio del producto
            System.out.print("Cantidad en stock: ");
            int cantidad = scanner.nextInt(); // Captura la cantidad del producto
            scanner.nextLine();  // Consume el salto de línea restante después del entero

            // Obtiene el elemento raíz del documento XML
            Element root = doc.getDocumentElement();

            // Crea un nuevo elemento de producto y añade subelementos
            Element producto = doc.createElement("producto");
            producto.appendChild(crearElemento(doc, "nombre", nombre.trim())); // Agrega nombre
            producto.appendChild(crearElemento(doc, "precio", String.valueOf(precio))); // Agrega precio
            producto.appendChild(crearElemento(doc, "cantidad", String.valueOf(cantidad))); // Agrega cantidad

            // Agrega el nuevo producto al documento XML
            root.appendChild(producto);
            guardarXML(doc); // Guarda los cambios en el archivo XML
            System.out.println("Producto agregado correctamente.");
        } catch (Exception e) {
            // Muestra un mensaje si ocurre un error al agregar el producto
            System.out.println("Error al agregar producto: " + e.getMessage());
        }
    }

    /**
     * Busca un producto por nombre en el archivo XML.
     */
    static void buscarProducto(Scanner scanner) throws IOException {
        try {
            // Carga el archivo XML para buscar el producto
            Document doc = cargarXML();
            System.out.print("Nombre del producto a buscar: ");
            String nombreBuscado = scanner.nextLine(); // Captura el nombre a buscar

            // Obtiene la lista de todos los elementos de producto en el XML
            // Búsqueda secuencial de productos en el archivo XML. Este acceso es eficiente para archivos pequeños.
            NodeList productos = doc.getElementsByTagName("producto");
            for (int i = 0; i < productos.getLength(); i++) { // Itera sobre cada producto
                Element producto = (Element) productos.item(i); // Obtiene cada elemento producto
                String nombre = producto.getElementsByTagName("nombre").item(0).getTextContent();

                // Compara el nombre actual con el nombre buscado
                if (nombre.equalsIgnoreCase(nombreBuscado)) {
                    // Si coincide, obtiene precio y cantidad y los muestra en la consola
                    String precio = producto.getElementsByTagName("precio").item(0).getTextContent();
                    String cantidad = producto.getElementsByTagName("cantidad").item(0).getTextContent();
                    System.out.println("Producto encontrado: " + nombre);
                    System.out.println("Precio: " + precio);
                    System.out.println("Cantidad en stock: " + cantidad);
                    return; // Sale de la función una vez encontrado el producto
                }
            }
            System.out.println("Producto no encontrado."); // Muestra mensaje si no encuentra el producto
        } catch (Exception e) {
            // Muestra un mensaje si ocurre un error durante la búsqueda
            System.out.println("Error al buscar producto: " + e.getMessage());
        }
    }

    /**
     * Modifica un producto existente en el archivo XML.
     */
    static void modificarProducto(Scanner scanner) throws IOException {
        try {
            // Carga el archivo XML para buscar el producto a modificar
            Document doc = cargarXML();
            System.out.print("Nombre del producto a modificar: ");
            String nombreBuscado = scanner.nextLine(); // Captura el nombre a modificar

            // Obtiene la lista de todos los productos en el XML
            // Búsqueda y modificación secuencial, adecuada para archivos pequeños y simples
            NodeList productos = doc.getElementsByTagName("producto");
            for (int i = 0; i < productos.getLength(); i++) { // Itera sobre cada producto
                Element producto = (Element) productos.item(i); // Obtiene cada elemento producto
                String nombre = producto.getElementsByTagName("nombre").item(0).getTextContent();

                // Si encuentra el producto, solicita los nuevos valores y actualiza
                if (nombre.equalsIgnoreCase(nombreBuscado)) {
                    System.out.print("Nuevo precio: ");
                    String nuevoPrecio = scanner.nextLine(); // Captura el nuevo precio
                    System.out.print("Nueva cantidad: ");
                    String nuevaCantidad = scanner.nextLine(); // Captura la nueva cantidad

                    // Actualiza los valores de precio y cantidad
                    producto.getElementsByTagName("precio").item(0).setTextContent(nuevoPrecio);
                    producto.getElementsByTagName("cantidad").item(0).setTextContent(nuevaCantidad);
                    guardarXML(doc); // Guarda los cambios en el archivo XML
                    System.out.println("Producto modificado.");
                    return; // Sale de la función después de modificar
                }
            }
            System.out.println("Producto no encontrado."); // Muestra mensaje si no encuentra el producto
        } catch (Exception e) {
            // Muestra un mensaje si ocurre un error al modificar el producto
            System.out.println("Error al modificar producto: " + e.getMessage());
        }
    }

    /**
     * Elimina un producto existente del archivo XML.
     */
    static void eliminarProducto(Scanner scanner) throws IOException {
        try {
            // Carga el archivo XML para buscar el producto a eliminar
            Document doc = cargarXML();
            System.out.print("Nombre del producto a eliminar: ");
            String nombreBuscado = scanner.nextLine(); // Captura el nombre a eliminar

            // Obtiene la lista de todos los productos en el XML
            // Búsqueda y eliminación secuencial, que es eficiente dado el tamaño del archivo XML en este caso
            NodeList productos = doc.getElementsByTagName("producto");
            for (int i = 0; i < productos.getLength(); i++) { // Itera sobre cada producto
                Element producto = (Element) productos.item(i); // Obtiene cada elemento producto
                String nombre = producto.getElementsByTagName("nombre").item(0).getTextContent();

                // Si encuentra el producto, lo elimina
                if (nombre.equalsIgnoreCase(nombreBuscado)) {
                    producto.getParentNode().removeChild(producto); // Elimina el producto
                    guardarXML(doc); // Guarda los cambios en el archivo XML
                    System.out.println("Producto eliminado.");
                    return; // Sale de la función después de eliminar
                }
            }
            System.out.println("Producto no encontrado."); // Muestra mensaje si no encuentra el producto
        } catch (Exception e) {
            // Muestra un mensaje si ocurre un error al eliminar el producto
            System.out.println("Error al eliminar producto: " + e.getMessage());
        }
    }

    /**
     * Exporta todos los productos a un archivo CSV.
     */
    static void exportarACSV() throws IOException {
        try {
            // Carga el archivo XML para obtener la lista de productos
            Document doc = cargarXML();
            NodeList productos = doc.getElementsByTagName("producto");

            // Crea un archivo CSV y agrega una cabecera con los nombres de las columnas
            FileWriter writer = new FileWriter("productos.csv");
            writer.write("Nombre,Precio,Cantidad\n");

            // Recorre cada producto en el XML y lo escribe en el archivo CSV
            for (int i = 0; i < productos.getLength(); i++) {
                Element producto = (Element) productos.item(i);
                String nombre = producto.getElementsByTagName("nombre").item(0).getTextContent();
                String precio = producto.getElementsByTagName("precio").item(0).getTextContent();
                String cantidad = producto.getElementsByTagName("cantidad").item(0).getTextContent();
                writer.write(nombre + "," + precio + "," + cantidad + "\n"); // Escribe en CSV
            }
            writer.close(); // Cierra el archivo CSV
            System.out.println("Productos exportados a productos.csv");
        } catch (Exception e) {
            // Muestra un mensaje si ocurre un error al exportar a CSV
            System.out.println("Error al exportar a CSV: " + e.getMessage());
        }
    }

    /**
     * Carga el archivo XML o lo crea si no existe.
     */
    static Document cargarXML() throws IOException {
        File archivo = new File("productos.xml"); // Crea un objeto File para el archivo XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // Obtiene una instancia de DocumentBuilderFactory
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder(); // Crea un DocumentBuilder para parsear el XML

            // Verifica si el archivo XML existe; si no, crea un nuevo archivo XML
            if (!archivo.exists()) {
                System.out.println("Archivo XML no encontrado. Creando nuevo archivo...");
                Document doc = builder.newDocument(); // Crea un nuevo documento
                Element root = doc.createElement("productos"); // Crea el elemento raíz "productos"
                doc.appendChild(root); // Añade el elemento raíz al documento
                guardarXML(doc); // Guarda el documento en un archivo
                return doc; // Retorna el documento recién creado
            }

            Document doc = builder.parse(archivo); // Parsear el XML existente
            doc.getDocumentElement().normalize(); // Normaliza la estructura XML
            return doc; // Retorna el documento cargado
        } catch (Exception e) {
            throw new IOException("Error al cargar el archivo XML: " + e.getMessage());
        }
    }

    /**
     * Guarda el documento XML en un archivo y limpia el contenido.
     */
    static void guardarXML(Document doc) throws IOException {
        try {
            // Configura las opciones de transformación para guardar el XML en formato legible
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // Activa la indentación
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            // Guarda el documento XML en el archivo "productos.xml"
            DOMSource source = new DOMSource(doc); // Fuente de datos
            StreamResult result = new StreamResult(new File("productos.xml")); // Archivo destino
            transformer.transform(source, result); // Realiza la transformación y guarda el archivo

            limpiarXML(); // Llama a limpiar el archivo XML después de guardarlo
        } catch (Exception e) {
            throw new IOException("Error al guardar el archivo XML: " + e.getMessage());
        }
    }

    /**
     * Limpia el archivo XML para eliminar espacios en blanco y líneas vacías.
     */
    static void limpiarXML() throws IOException {
        File inputFile = new File("productos.xml"); // Archivo XML original
        File tempFile = new File("productos_temp.xml"); // Archivo temporal para la limpieza

        // Lee el archivo original y escribe el contenido limpio en un archivo temporal
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) { // Lee línea por línea
                line = line.trim().replaceAll(">\\s+<", "><"); // Limpia los espacios entre etiquetas
                if (!line.isEmpty()) { // Omite líneas vacías
                    writer.write(line); // Escribe en el archivo temporal
                    writer.newLine(); // Añade salto de línea
                }
            }
        }

        // Reemplaza el archivo original con el archivo limpio
        if (!inputFile.delete()) { // Elimina el archivo original
            throw new IOException("No se pudo eliminar el archivo original");
        }
        if (!tempFile.renameTo(inputFile)) { // Renombra el archivo temporal
            throw new IOException("No se pudo renombrar el archivo temporal");
        }
    }

    /**
     * Crea un nuevo elemento XML con nombre y valor específicos.
     */
    static Element crearElemento(Document doc, String nombre, String valor) {
        Element elemento = doc.createElement(nombre); // Crea un nuevo elemento XML con el nombre especificado
        elemento.setTextContent(valor.trim()); // Asigna el valor al elemento, eliminando espacios en blanco
        return elemento; // Retorna el elemento creado
    }
}