package sample.aop.event;

import sample.aop.domain.AbstractPolicy;

/**
 * Created by n0292928 on 8/31/16.
 */
public interface IPolicyEvent {


    public String getEventType();
    public AbstractPolicy getPolicy();

}
