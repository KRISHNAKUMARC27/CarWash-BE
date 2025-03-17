"use strict";(self.webpackChunkberry_free_material_react_cra=self.webpackChunkberry_free_material_react_cra||[]).push([[2298],{22298:function(e,t,n){n.r(t);var i=n(72791),a=n(5574),l=n(65661),o=n(39157),r=n(61889),s=n(48550),d=n(23786),c=n(13400),m=n(20890),u=n(94294),p=n(97123),y=n(91923),h=n(19035),x=n(22840),g=n(6879),j=n(80184);t.default=e=>{let{invoice:t,setInvoice:n,paymentModes:v,invoiceCreateOpen:Z,handleClose:C,setAlertMess:P,setShowAlert:f,setAlertColor:L}=e;const[A,S]=(0,i.useState)(!1),[b,M]=(0,i.useState)(0);(0,i.useEffect)((()=>()=>{M(0)}),[]);const T=(e,i,a)=>{const l=[...t.paymentSplitList];l[e]={...l[e],[i]:a};const o=l.filter((e=>"CREDIT"!==e.paymentMode)).reduce(((e,t)=>e+(t.paymentAmount||0)),0),r=(t.grandTotal||0)-o;n((e=>({...e,paymentSplitList:l,pendingAmount:r>0?r:0,creditFlag:l.some((e=>"CREDIT"===e.paymentMode))})))},I=()=>{const e=(t.grandTotal||0)-t.paymentSplitList.reduce(((e,t)=>e+(t.paymentAmount||0)),0);e<=0?alert("No remaining amount to allocate. Please adjust the existing payment splits."):n((t=>({...t,paymentSplitList:[...t.paymentSplitList,{paymentAmount:e,paymentMode:""}]})))},k=()=>{S(!1)},R=(e,i,a)=>{const l=[...t.creditPaymentList];l[e]={...l[e],[i]:a},n((e=>({...e,creditPaymentList:l})))};return(0,j.jsxs)(j.Fragment,{children:[Z&&(0,j.jsxs)(a.Z,{open:Z,onClose:C,scroll:"paper","aria-labelledby":"scroll-dialog-title","aria-describedby":"scroll-dialog-description",fullWidth:!0,maxWidth:"lg",children:[(0,j.jsxs)(l.Z,{id:"scroll-dialog-title",sx:{fontSize:"1.0rem"},children:["Invoice Generation for ",t.vehicleRegNo]}),(0,j.jsxs)(o.Z,{dividers:"paper"===scroll,children:[(0,j.jsx)("br",{}),(0,j.jsxs)(r.ZP,{container:!0,direction:"row",spacing:y.dv,children:[(0,j.jsx)(r.ZP,{item:!0,xs:6,children:(0,j.jsx)(s.Z,{label:"Grand Total",required:!0,variant:"outlined",value:(null===t||void 0===t?void 0:t.grandTotal)||0})}),t.paymentSplitList.map(((e,i)=>(0,j.jsxs)(r.ZP,{container:!0,item:!0,spacing:y.dv,alignItems:"center",children:[(0,j.jsx)(r.ZP,{item:!0,xs:5,children:(0,j.jsx)(s.Z,{label:"Payment Amount",variant:"outlined",fullWidth:!0,required:!0,value:e.paymentAmount,onChange:e=>T(i,"paymentAmount",parseFloat(e.target.value)||0),type:"number"})}),(0,j.jsx)(r.ZP,{item:!0,xs:5,children:(0,j.jsx)(s.Z,{select:!0,label:"Payment Mode",variant:"outlined",fullWidth:!0,required:!0,value:e.paymentMode,onChange:e=>T(i,"paymentMode",e.target.value),children:v.map((e=>(0,j.jsx)(d.Z,{value:e,children:e},e)))})}),(0,j.jsx)(r.ZP,{item:!0,xs:2,children:0===i?(0,j.jsx)(c.Z,{onClick:I,color:"primary",children:(0,j.jsx)(x.Z,{})}):(0,j.jsx)(c.Z,{onClick:()=>(e=>{const i=t.paymentSplitList.filter(((t,n)=>n!==e));n((e=>({...e,paymentSplitList:i})))})(i),color:"secondary",children:(0,j.jsx)(g.Z,{})})})]},i)))]}),(0,j.jsx)("br",{}),t.creditFlag&&(0,j.jsxs)(r.ZP,{container:!0,direction:"row",spacing:y.dv,children:[(0,j.jsx)(r.ZP,{item:!0,xs:4,children:(0,j.jsx)(m.Z,{variant:"h4",children:"Credit Payment"})}),(t.creditPaymentList||[]).map(((e,i)=>(0,j.jsxs)(r.ZP,{container:!0,item:!0,spacing:y.dv,alignItems:"center",children:[(0,j.jsx)(r.ZP,{item:!0,xs:4,children:(0,j.jsx)(s.Z,{label:"Credit Amount",variant:"outlined",fullWidth:!0,required:!0,value:e.amount,onChange:e=>R(i,"amount",parseFloat(e.target.value)||0),type:"number"})}),(0,j.jsx)(r.ZP,{item:!0,xs:3,children:(0,j.jsx)(s.Z,{select:!0,label:"Payment Mode",variant:"outlined",fullWidth:!0,required:!0,value:e.paymentMode,onChange:e=>R(i,"paymentMode",e.target.value),children:v.filter((e=>"CREDIT"!==e)).map((e=>(0,j.jsx)(d.Z,{value:e,children:e},e)))})}),(0,j.jsx)(r.ZP,{item:!0,xs:4,children:(0,j.jsx)(s.Z,{label:"Comment",variant:"outlined",fullWidth:!0,value:e.comment||"",onChange:e=>R(i,"comment",e.target.value)})}),(0,j.jsx)(r.ZP,{item:!0,xs:1,children:(0,j.jsx)(c.Z,{onClick:()=>(e=>{const i=t.creditPaymentList.filter(((t,n)=>n!==e)),a=i.reduce(((e,t)=>e+(t.amount||0)),0),l=(t.grandTotal||0)-a;n((e=>({...e,creditPaymentList:i,pendingAmount:l>0?l:0,creditSettledFlag:0===l})))})(i),color:"secondary",children:(0,j.jsx)(g.Z,{})})})]},i))),(0,j.jsx)(r.ZP,{item:!0,xs:12,children:(0,j.jsx)(u.Z,{onClick:()=>{const e=(t.grandTotal||0)-t.paymentSplitList.filter((e=>"CREDIT"!==e.paymentMode)).reduce(((e,t)=>e+(t.paymentAmount||0)),0)-t.creditPaymentList.reduce(((e,t)=>e+(t.amount||0)),0);e<=0?alert("No remaining amount to allocate. Please adjust the existing payment splits."):n((t=>({...t,creditPaymentList:[...t.creditPaymentList||[],{amount:e,paymentMode:"",comment:""}]})))},color:"primary",startIcon:(0,j.jsx)(x.Z,{}),children:"Add Credit Payment"})})]})]}),(0,j.jsxs)(p.Z,{children:[(0,j.jsx)(u.Z,{onClick:async()=>{if(t.grandTotal<=0)return void alert("Grant total is 0. Cannot generate bill");const e=t.grandTotal||0,n=e-t.paymentSplitList.reduce(((e,t)=>e+(t.paymentAmount||0)),0);console.log("REMAINING "+n);if(t.paymentSplitList.some((e=>!e.paymentMode)))return void alert("Please select a payment mode for all entries.");if(n>0)return console.log("I'm still open"),console.log(t),void(e=>{M(e),S(!0)})(n);if(n<0)return void alert("Payment exceeds the grand total. Please adjust the amounts.");const i=[...t.creditPaymentList];if(i.some((e=>!e.paymentMode)))return void alert("Please select a payment mode for all entries.");const a=i.reduce(((e,t)=>e+(t.amount||0)),0),l=[...t.paymentSplitList].filter((e=>"CREDIT"!==e.paymentMode)).reduce(((e,t)=>e+(t.paymentAmount||0)),0),o=e-l-a;console.log("GrandTotal "+e),console.log("totalPaidExcludingCredit "+l),console.log("totalCreditPayments"+a),console.log("newPendingAmount "+o);const r={...t,pendingAmount:o>0?o:0,creditSettledFlag:!(o>0)};try{const e=await(0,h.j0)("/invoice",r);P("Bill id "+e.invoiceId+" saved successfully"),L("success"),f(!0),C()}catch(s){P(s.message),L("info"),f(!0),C()}},color:"secondary",children:"Save"}),(0,j.jsx)(u.Z,{onClick:C,color:"secondary",children:"Close"})]})]}),A&&(0,j.jsxs)(a.Z,{open:A,onClose:k,children:[(0,j.jsx)(l.Z,{children:"Confirm Remaining Amount"}),(0,j.jsx)(o.Z,{children:(0,j.jsxs)("p",{children:["The remaining amount of ",(0,j.jsx)("b",{children:b})," will be added as CREDIT. Do you want to proceed?"]})}),(0,j.jsxs)(p.Z,{children:[(0,j.jsx)(u.Z,{onClick:()=>{S(!1),n((e=>({...e,paymentSplitList:[...e.paymentSplitList,{paymentAmount:b,paymentMode:"CREDIT"}],pendingAmount:b,creditFlag:!0})))},color:"primary",children:"Yes"}),(0,j.jsx)(u.Z,{onClick:k,color:"secondary",children:"No"})]})]})]})}},6879:function(e,t,n){var i=n(76189),a=n(80184);t.Z=(0,i.Z)((0,a.jsx)("path",{d:"M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 11H7v-2h10v2z"}),"RemoveCircle")}}]);
//# sourceMappingURL=2298.53e9bb51.chunk.js.map