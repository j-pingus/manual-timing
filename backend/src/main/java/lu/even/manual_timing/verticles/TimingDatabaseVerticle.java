package lu.even.manual_timing.verticles;

import lu.even.manual_timing.events.EventAction;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimingDatabaseVerticle extends AbstractTimingVerticle {
  protected static final Logger logger = LoggerFactory.getLogger(TimingDatabaseVerticle.class);

  public TimingDatabaseVerticle() {
    super(EventTypes.DATABASE);
    setRespond(false);
  }

  @Override
  protected Object onMessage(EventTypes eventType, EventMessage message) {
    if (message.action() == EventAction.SAVE_TIME) {
      logger.info("Saving in DB:{}", message);
    }else{
      logger.debug("Not saving in DB:{}", message);
    }
    return null;
  }
}
