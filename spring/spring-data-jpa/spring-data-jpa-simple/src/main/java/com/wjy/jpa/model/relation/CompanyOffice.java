package com.wjy.jpa.model.relation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

@Entity
@Data
@Builder
//@IdClass(UserInfoID.class)
@AllArgsConstructor
@NoArgsConstructor
public class CompanyOffice {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumns({
         @JoinColumn(name="addr_id", referencedColumnName="id"),
         @JoinColumn(name="addr_zip", referencedColumnName="zip")
   })
   private CompanyAddress address;

}
