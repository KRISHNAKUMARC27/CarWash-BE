"use strict";(self.webpackChunkcar_wash_management=self.webpackChunkcar_wash_management||[]).push([[8203,5840],{58203:function(e,t,n){n.r(t);var a=n(72791),i=n(5574),l=n(65661),s=n(39157),o=n(61889),r=n(48550),d=n(23786),c=n(13400),m=n(20890),u=n(94294),p=n(97123),h=n(91923),y=n(19035),x=n(22840),g=n(6879),j=n(80184);t.default=e=>{let{estimate:t,setEstimate:n,paymentModes:Z,estimateCreateOpen:v,handleClose:C,setAlertMess:P,setShowAlert:f,setAlertColor:A}=e;const[L,S]=(0,a.useState)(!1),[M,b]=(0,a.useState)(0);(0,a.useEffect)((()=>()=>{b(0)}),[]);const T=(e,a,i)=>{const l=[...t.paymentSplitList];l[e]={...l[e],[a]:i};const s=l.filter((e=>"CREDIT"!==e.paymentMode)).reduce(((e,t)=>e+(t.paymentAmount||0)),0),o=(t.grandTotal||0)-s;n((e=>({...e,paymentSplitList:l,pendingAmount:o>0?o:0,creditFlag:l.some((e=>"CREDIT"===e.paymentMode))})))},I=()=>{const e=(t.grandTotal||0)-t.paymentSplitList.reduce(((e,t)=>e+(t.paymentAmount||0)),0);e<=0?alert("No remaining amount to allocate. Please adjust the existing payment splits."):n((t=>({...t,paymentSplitList:[...t.paymentSplitList,{paymentAmount:e,paymentMode:""}]})))},k=()=>{S(!1)},E=(e,a,i)=>{const l=[...t.creditPaymentList];l[e]={...l[e],[a]:i},n((e=>({...e,creditPaymentList:l})))};return(0,j.jsxs)(j.Fragment,{children:[v&&(0,j.jsxs)(i.Z,{open:v,onClose:C,scroll:"paper","aria-labelledby":"scroll-dialog-title","aria-describedby":"scroll-dialog-description",fullWidth:!0,maxWidth:"lg",children:[(0,j.jsxs)(l.Z,{id:"scroll-dialog-title",sx:{fontSize:"1.0rem"},children:["Estimate Generation for ",t.vehicleRegNo]}),(0,j.jsxs)(s.Z,{dividers:"paper"===scroll,children:[(0,j.jsx)("br",{}),(0,j.jsxs)(o.ZP,{container:!0,direction:"row",spacing:h.dv,children:[(0,j.jsx)(o.ZP,{item:!0,xs:6,children:(0,j.jsx)(r.Z,{label:"Grand Total",required:!0,variant:"outlined",value:(null===t||void 0===t?void 0:t.grandTotal)||0})}),t.paymentSplitList.map(((e,a)=>(0,j.jsxs)(o.ZP,{container:!0,item:!0,spacing:h.dv,alignItems:"center",children:[(0,j.jsx)(o.ZP,{item:!0,xs:5,children:(0,j.jsx)(r.Z,{label:"Payment Amount",variant:"outlined",fullWidth:!0,required:!0,value:e.paymentAmount,onChange:e=>T(a,"paymentAmount",parseFloat(e.target.value)||0),type:"number"})}),(0,j.jsx)(o.ZP,{item:!0,xs:5,children:(0,j.jsx)(r.Z,{select:!0,label:"Payment Mode",variant:"outlined",fullWidth:!0,required:!0,value:e.paymentMode,onChange:e=>T(a,"paymentMode",e.target.value),children:Z.map((e=>(0,j.jsx)(d.Z,{value:e,children:e},e)))})}),(0,j.jsx)(o.ZP,{item:!0,xs:2,children:0===a?(0,j.jsx)(c.Z,{onClick:I,color:"primary",children:(0,j.jsx)(x.Z,{})}):(0,j.jsx)(c.Z,{onClick:()=>(e=>{const a=t.paymentSplitList.filter(((t,n)=>n!==e));n((e=>({...e,paymentSplitList:a})))})(a),color:"secondary",children:(0,j.jsx)(g.Z,{})})})]},a)))]}),(0,j.jsx)("br",{}),t.creditFlag&&(0,j.jsxs)(o.ZP,{container:!0,direction:"row",spacing:h.dv,children:[(0,j.jsx)(o.ZP,{item:!0,xs:4,children:(0,j.jsx)(m.Z,{variant:"h4",children:"Credit Payment"})}),(t.creditPaymentList||[]).map(((e,a)=>(0,j.jsxs)(o.ZP,{container:!0,item:!0,spacing:h.dv,alignItems:"center",children:[(0,j.jsx)(o.ZP,{item:!0,xs:4,children:(0,j.jsx)(r.Z,{label:"Credit Amount",variant:"outlined",fullWidth:!0,required:!0,value:e.amount,onChange:e=>E(a,"amount",parseFloat(e.target.value)||0),type:"number"})}),(0,j.jsx)(o.ZP,{item:!0,xs:3,children:(0,j.jsx)(r.Z,{select:!0,label:"Payment Mode",variant:"outlined",fullWidth:!0,required:!0,value:e.paymentMode,onChange:e=>E(a,"paymentMode",e.target.value),children:Z.filter((e=>"CREDIT"!==e)).map((e=>(0,j.jsx)(d.Z,{value:e,children:e},e)))})}),(0,j.jsx)(o.ZP,{item:!0,xs:4,children:(0,j.jsx)(r.Z,{label:"Comment",variant:"outlined",fullWidth:!0,value:e.comment||"",onChange:e=>E(a,"comment",e.target.value)})}),(0,j.jsx)(o.ZP,{item:!0,xs:1,children:(0,j.jsx)(c.Z,{onClick:()=>(e=>{const a=t.creditPaymentList.filter(((t,n)=>n!==e)),i=a.reduce(((e,t)=>e+(t.amount||0)),0),l=(t.grandTotal||0)-i;n((e=>({...e,creditPaymentList:a,pendingAmount:l>0?l:0,creditSettledFlag:0===l})))})(a),color:"secondary",children:(0,j.jsx)(g.Z,{})})})]},a))),(0,j.jsx)(o.ZP,{item:!0,xs:12,children:(0,j.jsx)(u.Z,{onClick:()=>{const e=(t.grandTotal||0)-t.paymentSplitList.filter((e=>"CREDIT"!==e.paymentMode)).reduce(((e,t)=>e+(t.paymentAmount||0)),0)-t.creditPaymentList.reduce(((e,t)=>e+(t.amount||0)),0);e<=0?alert("No remaining amount to allocate. Please adjust the existing payment splits."):n((t=>({...t,creditPaymentList:[...t.creditPaymentList||[],{amount:e,paymentMode:"",comment:""}]})))},color:"primary",startIcon:(0,j.jsx)(x.Z,{}),children:"Add Credit Payment"})})]})]}),(0,j.jsxs)(p.Z,{children:[(0,j.jsx)(u.Z,{onClick:async()=>{if(t.grandTotal<=0)return void alert("Grant total is 0. Cannot generate bill");const e=t.grandTotal||0,n=e-t.paymentSplitList.reduce(((e,t)=>e+(t.paymentAmount||0)),0);console.log("REMAINING "+n);if(t.paymentSplitList.some((e=>!e.paymentMode)))return void alert("Please select a payment mode for all entries.");if(n>0)return console.log("I'm still open"),console.log(t),void(e=>{b(e),S(!0)})(n);if(n<0)return void alert("Payment exceeds the grand total. Please adjust the amounts.");const a=[...t.creditPaymentList];if(a.some((e=>!e.paymentMode)))return void alert("Please select a payment mode for all entries.");const i=a.reduce(((e,t)=>e+(t.amount||0)),0),l=[...t.paymentSplitList].filter((e=>"CREDIT"!==e.paymentMode)).reduce(((e,t)=>e+(t.paymentAmount||0)),0),s=e-l-i;console.log("GrandTotal "+e),console.log("totalPaidExcludingCredit "+l),console.log("totalCreditPayments"+i),console.log("newPendingAmount "+s);const o={...t,pendingAmount:s>0?s:0,creditSettledFlag:!(s>0)};try{const e=await(0,y.j0)("/estimate",o);P("Bill id "+e.estimateId+" saved successfully"),A("success"),f(!0),C()}catch(r){P(r.message),A("info"),f(!0),C()}},color:"secondary",children:"Save"}),(0,j.jsx)(u.Z,{onClick:C,color:"secondary",children:"Close"})]})]}),L&&(0,j.jsxs)(i.Z,{open:L,onClose:k,children:[(0,j.jsx)(l.Z,{children:"Confirm Remaining Amount"}),(0,j.jsx)(s.Z,{children:(0,j.jsxs)("p",{children:["The remaining amount of ",(0,j.jsx)("b",{children:M})," will be added as CREDIT. Do you want to proceed?"]})}),(0,j.jsxs)(p.Z,{children:[(0,j.jsx)(u.Z,{onClick:()=>{S(!1),n((e=>({...e,paymentSplitList:[...e.paymentSplitList,{paymentAmount:M,paymentMode:"CREDIT"}],pendingAmount:M,creditFlag:!0})))},color:"primary",children:"Yes"}),(0,j.jsx)(u.Z,{onClick:k,color:"secondary",children:"No"})]})]})]})}},22840:function(e,t,n){var a=n(76189),i=n(80184);t.Z=(0,a.Z)((0,i.jsx)("path",{d:"M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 11h-4v4h-2v-4H7v-2h4V7h2v4h4v2z"}),"AddCircle")},6879:function(e,t,n){var a=n(76189),i=n(80184);t.Z=(0,a.Z)((0,i.jsx)("path",{d:"M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 11H7v-2h10v2z"}),"RemoveCircle")}}]);
//# sourceMappingURL=8203.908687b8.chunk.js.map