"use strict";(self.webpackChunkberry_free_material_react_cra=self.webpackChunkberry_free_material_react_cra||[]).push([[811],{50811:function(e,l,t){t.r(l);var a=t(5574),r=t(65661),n=t(39157),i=t(61889),s=t(48550),o=t(23786),c=t(97123),d=t(94294),m=t(91923),u=t(19035),h=t(80184);l.default=e=>{let{receipt:l,setReceipt:t,paymentModes:p,receiptDialogOpen:x,selectedRows:j,handleClose:v,setAlertMess:g,setShowAlert:f}=e;const Z=(e,a)=>{const r={...l,[e]:a};t(r)};return(0,h.jsx)(h.Fragment,{children:x&&(0,h.jsxs)(a.Z,{open:x,onClose:v,scroll:"paper","aria-labelledby":"scroll-dialog-title","aria-describedby":"scroll-dialog-description",fullWidth:!0,maxWidth:"md",children:[(0,h.jsxs)(r.Z,{id:"scroll-dialog-title",sx:{fontSize:"1.0rem"},children:["Receipts for ",j.map((e=>e.estimateId)).join(", ")]}),(0,h.jsxs)(n.Z,{dividers:"paper"===scroll,children:[(0,h.jsx)("br",{}),(0,h.jsxs)(i.ZP,{container:!0,item:!0,spacing:m.dv,alignItems:"center",children:[(0,h.jsx)(i.ZP,{item:!0,xs:4,children:(0,h.jsx)(s.Z,{label:"ReceiptTo",variant:"outlined",fullWidth:!0,value:l.ownerName||"",onChange:e=>Z("ownerName",e.target.value)})}),(0,h.jsx)(i.ZP,{item:!0,xs:4,children:(0,h.jsx)(s.Z,{label:"Credit Amount",variant:"outlined",fullWidth:!0,required:!0,value:l.amount||0,onChange:e=>Z("amount",parseFloat(e.target.value)||0),type:"number"})}),(0,h.jsx)(i.ZP,{item:!0,xs:3,children:(0,h.jsx)(s.Z,{select:!0,label:"Payment Mode",variant:"outlined",fullWidth:!0,required:!0,value:l.paymentMode||"",onChange:e=>Z("paymentMode",e.target.value),children:[...new Set([...p.filter((e=>"CREDIT"!==e)),"MULTI"])].map((e=>(0,h.jsx)(o.Z,{value:e,children:e},e)))})}),(0,h.jsx)(i.ZP,{item:!0,xs:4,children:(0,h.jsx)(s.Z,{label:"Comment",variant:"outlined",fullWidth:!0,value:l.comment||"",onChange:e=>Z("comment",e.target.value)})})]})]}),(0,h.jsxs)(c.Z,{children:[(0,h.jsx)(d.Z,{onClick:async()=>{if(null!=l.paymentMode)if(null==l.amount||l.amount<=0)alert("Enter valid amount");else try{const e=await(0,u.j0)("/estimate/receipt",l);g("Receipt No."+e.id+" is generated"),f(!0),v()}catch(e){g(e.message),f(!0),v()}else alert("Please select a payment mode.")},color:"secondary",children:"Save"}),(0,h.jsx)(d.Z,{onClick:v,color:"secondary",children:"Close"})]})]})})}}}]);
//# sourceMappingURL=811.97ac1272.chunk.js.map