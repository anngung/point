package mileage;

import javax.persistence.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "Point_table")
public class Point {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;
        private Long memberId;
        private Long remainPoint =1000L;
        private String memberStatus;
        private Long requirePoint;
        private Long refundPoint;

        @PostPersist
        public void onPostPersist() {
                System.out.println("\n$$$onPostPersist");


                if(this.refundPoint < 0) {

                        System.out.println("\n$$$onPostPersist");

                        PointSaved pointSaved = new PointSaved();
                        BeanUtils.copyProperties(this, pointSaved);
                        pointSaved.setMemberId(memberId);
                        pointSaved.setRemainPoint(remainPoint - refundPoint);
                        pointSaved.publishAfterCommit();

                }
                else{

                        if(this.memberStatus.equals("NORMAL")) {

                                PointSaved pointSaved = new PointSaved();
                                BeanUtils.copyProperties(this, pointSaved);
                                pointSaved.publishAfterCommit();
                        }else if(this.memberStatus.equals("WITHDRAWAL")){
                                PointSaved pointSaved = new PointSaved();
                                BeanUtils.copyProperties(this, pointSaved);
                                pointSaved.setMemberId(this.getId());
                                pointSaved.setMemberId(this.getMemberId());
                                pointSaved.setRemainPoint(0L);
                                pointSaved.publishAfterCommit();
                        }
                }



        }

        //@PrePersist
        //public void onPrePersist() {
        @PreUpdate
        public void onPreUpdate() {


                if(this.refundPoint < 0) {
                        System.out.println("\n$$$onPostPersist" + this.refundPoint);
                        RefundCancelled refundCancelled = new RefundCancelled();
                        BeanUtils.copyProperties(this, refundCancelled);
                        //refundCancelled.setMemberStatus("WITHDRAWAL");
                        refundCancelled.publishAfterCommit();

                        //Following code causes dependency to external APIs
                        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.
                        mileage.external.Refund refund = new mileage.external.Refund();
                        // mappings goes here

                        refund.setMemberId(this.getMemberId());
                        refund.setRemainPoint(this.getRemainPoint());
                        refund.setMemberStatus(this.getMemberStatus());
                        refund.setRefundPoint(this.getRefundPoint());

                        System.out.println("\ngetMemberId" + this.getMemberId());

                        PointApplication.applicationContext.getBean(mileage.external.RefundService.class).forfeit(refund);


                }

        }

                /*
        @PreUpdate
        public void onPreUpdate() {


                Long newPoint = remainPoint;

                PointUsed pointUsed = new PointUsed();

                if(this.memberStatus.equals("NORMAL")) {

                        newPoint = this.getRemainPoint() + this.getRequirePoint();
                        this.setRemainPoint(newPoint);
                        BeanUtils.copyProperties(this, pointUsed);
                        pointUsed.publishAfterCommit();
                }

                System.out.println("\n$$$onPreUpdate : " + newPoint);



        }
                */
        @PostUpdate
        public void onPostUpdate(){

                System.out.println("remainPoint : " + this.remainPoint);
                System.out.println("refundPoint : " + this.refundPoint);

                if(this.refundPoint > 0) {

                        RefundApplied refundApplied = new RefundApplied();
                        BeanUtils.copyProperties(this, refundApplied);
                        refundApplied.publishAfterCommit();

                }

        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public Long getMemberId() {
                return memberId;
        }

        public void setMemberId(Long memberId) {
                this.memberId = memberId;
        }

        public Long getRemainPoint() {
                return remainPoint;
        }

        public void setRemainPoint(Long remainPoint) {
                this.remainPoint = remainPoint;
        }

        public String getMemberStatus() {
                return memberStatus;
        }

        public void setMemberStatus(String memberStatus) {
                this.memberStatus = memberStatus;
        }

        public Long getRequirePoint() {
                return requirePoint;
        }

        public void setRequirePoint(Long usePoint) {
                this.requirePoint = usePoint;
        }
        public Long getRefundPoint() {
                return refundPoint;
        }

        public void setRefundPoint(Long refundPoint) {
                this.refundPoint = refundPoint;
        }
}