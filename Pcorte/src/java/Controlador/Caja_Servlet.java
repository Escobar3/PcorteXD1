/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import DAO.*;
import VO.*;
import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import java.sql.SQLException;
import static java.time.temporal.TemporalQueries.localDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/**
 *
 * @author LUIS
 */
public class Caja_Servlet extends HttpServlet {

    ProductoDAO producto;
    VendedorDAO vendedor;
    Item_ventDAO item;
    CajaDAO caja;
    VentaDAO venta;
    private Venta ven;
    private List<Producto> listProdutos;
    List<Producto> inven = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        this.producto = new ProductoDAO();
        this.venta = new VentaDAO();
        this.vendedor = new VendedorDAO();
        this.caja = new CajaDAO();
        this.item = new Item_ventDAO();

    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.sql.SQLException
     */
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            listProdutos = producto.findAll();
        } catch (SQLException ex) {
            Logger.getLogger(Caja_Servlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        request.setAttribute("p2", listProdutos);

        request.getRequestDispatcher("Caja.jsp").forward(request, response);
        doPost(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String txtValop = request.getParameter("txtValOpe");
        JSONArray varJsonArrayP = new JSONArray();
        response.setContentType("text/html");
        PrintWriter escritor = response.getWriter();
        String idP = request.getParameter("productos");
        String idC = request.getParameter("idCaja");
        String UserV = request.getParameter("UserV");
        String p = request.getParameter("p");
        String f = request.getParameter("fecha");
        String can = request.getParameter("unds");
        System.out.println(can);
        if (txtValop != null) {
            try {

                int unds = Integer.parseInt(can);

                Item_vent itemDeVenda = new Item_vent();
                System.out.println(idP);
                System.out.println(idC);
                System.out.println(Integer.parseInt(idC));
                Caja aux = caja.find(Integer.parseInt(idC));
                Vendedor vend = vendedor.find(UserV);
                Producto p1 = producto.find(Integer.parseInt(idP));
                double pre = aux.calP(p1.getPrecio(), unds);
                p1.setCantidad(unds);
                p1.setPrecio(pre);
                itemDeVenda.setProducto(p1);
                itemDeVenda.setCantidad(p1.getCantidad());
                itemDeVenda.setValor(pre);
                inven.add(p1);
                JSONArray  varJObjectLista = metGetLista(inven, varJsonArrayP);
                escritor.print(varJObjectLista);
            } catch (SQLException ex) {
                Logger.getLogger(Caja_Servlet.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    public JSONArray metGetLista(List<Producto> in, JSONArray varJsonArrayP) {
       
        JSONObject varJsonObjectResultado = new JSONObject();
        try {
            for (int i = 0; i < in.size(); i++) {
                 JSONObject varJsonObjectP = new JSONObject();

                System.out.println("------------------------");
                Producto p = in.get(i);
                varJsonObjectP.put("id", p.getId_producto());
                varJsonObjectP.put("nombre", p.getNombre());
                varJsonObjectP.put("cantidad", p.getCantidad());
                varJsonObjectP.put("precio", p.getPrecio());
                varJsonArrayP.add(varJsonObjectP);
                varJsonObjectP =( JSONObject) varJsonArrayP.get(i);
                System.out.println("-------------------");
                System.out.println( varJsonObjectP.toJSONString());
                System.out.println("-----------------------------");
                    System.out.println(varJsonArrayP.get(i));
               
            }
            varJsonObjectResultado.put("Result", "OK");
            varJsonObjectResultado.put("Records", varJsonArrayP);
        } catch (Exception e) {
            e.printStackTrace();
            varJsonObjectResultado.put("Result", "ERROR");
            varJsonObjectResultado.put("Message", e.getMessage());
        }
        return varJsonArrayP;
    }
}