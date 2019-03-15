/**
 * Pr&aacute;ctricas de Sistemas Inform&aacute;ticos II
 * VisaCancelacionJMSBean.java
 */

package ssii2.visa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.ActivationConfigProperty;
import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.JMSException;
import javax.annotation.Resource;
import java.util.logging.Logger;

/**
 * @author jaime
 */
@MessageDriven(mappedName = "jms/VisaPagosQueue")
public class VisaCancelacionJMSBean extends DBTester implements MessageListener {
  static final Logger logger = Logger.getLogger("VisaCancelacionJMSBean");
  @Resource
  private MessageDrivenContext mdc;

   // TODO : Definir UPDATE sobre la tabla pagos para poner
   // codRespuesta a 999 dado un código de autorización
  private static final String UPDATE_CANCELA_QRY =
                "update pago " +
                "set codRespuesta=999 " +
                "where idAutorizacion=? ";

  private static final String UPDATE_RECTIFICAR_SALDO_QRY =
                 "update tarjeta " +
                    "set saldo=? " +
                    "where numeroTarjeta=? ";

private static final String SELECT_TARJETA_IDAUTORIZACION_QRY = 
                    "select numeroTarjeta" +
                    " from pago" +
                    " where idAutorizacion = ?";

private static final String SELECT_SALDO_QRY = 
                    "select saldo" +
                    " from tarjeta" +
                    " where numeroTarjeta = ?";

private static final String SELECT_IMPORTE_QRY = 
                    "select importe" +
                    " from pago" +
                    " where idAutorizacion = ?";

  public VisaCancelacionJMSBean() {
  }

  // TODO : Método onMessage de ejemplo
  // Modificarlo para ejecutar el UPDATE definido más arriba,
  // asignando el idAutorizacion a lo recibido por el mensaje
  // Para ello conecte a la BD, prepareStatement() y ejecute correctamente
  // la actualización
  public void onMessage(Message inMessage) {
      TextMessage msg = null;
      PreparedStatement pstmt = null;
      Connection con = null;
      double saldo, importe;
      ResultSet result = null;
      int res;
      String tarjeta;

      try {
          // Crear una conexion u obtenerla del pool
          con = getConnection();

          if (inMessage instanceof TextMessage) {

            msg = (TextMessage) inMessage;
            logger.info("MESSAGE BEAN: Message received: " + msg.getText());

            String insert  = UPDATE_CANCELA_QRY;
            logger.info(insert);

            pstmt = con.prepareStatement(insert);
            pstmt.setInt(1, Integer.parseInt(msg.getText()));

            pstmt.execute();

            /*Conseguimos el numero de tajeta*/
            insert = SELECT_TARJETA_IDAUTORIZACION_QRY;
            logger.info(insert);
            pstmt = con.prepareStatement(insert);
            

            pstmt.setInt(1, Integer.parseInt(msg.getText()));
            result = pstmt.executeQuery();

            if(result.next()){
            	tarjeta = result.getString("numeroTarjeta");
            } else{
            	throw new JMSException("Error, esta tarjeta no ha reaizado ningun pago");
            }

            /*Conseguimos el saldo de la transaccion*/
            insert = SELECT_SALDO_QRY;
            logger.info(insert);
            pstmt = con.prepareStatement(insert);
            

            pstmt.setString(1, tarjeta);
            result = pstmt.executeQuery();

            if(result.next()){
            	saldo = result.getDouble("saldo");
            } else{
            	throw new JMSException("Error al capturar el saldo de la tarjeta");
            }

            /*Conseguimos el importe de la operacion cancelada*/
            insert = SELECT_IMPORTE_QRY;
            logger.info(insert);
            pstmt = con.prepareStatement(insert);
            

            pstmt.setInt(1, Integer.parseInt(msg.getText()));
            result = pstmt.executeQuery();

            if(result.next()){
            	importe = result.getDouble("importe");
            } else{
            	throw new JMSException("Error al capturar el importe de la operacion cancelada");
            }

            /*Restauramos el saldo*/
            saldo += importe;

            insert  = UPDATE_RECTIFICAR_SALDO_QRY;
            logger.info(insert);

            pstmt = con.prepareStatement(insert);
            pstmt.setDouble(1, saldo);
            pstmt.setString(2, tarjeta);

            res = pstmt.executeUpdate();
  
          } else {
              logger.warning(
                      "Message of wrong type: "
                      + inMessage.getClass().getName());
          }
      } catch (JMSException e) {
          e.printStackTrace();
          mdc.setRollbackOnly();
      } catch (Throwable te) {
          te.printStackTrace();
      }
  }


}
