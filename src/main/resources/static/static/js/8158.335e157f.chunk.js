"use strict";(self.webpackChunkberry_free_material_react_cra=self.webpackChunkberry_free_material_react_cra||[]).push([[8158],{78158:function(e,t,r){r.r(t);var a=r(72791),s=r(68899),o=r(13967),c=r(61979),i=r(44507),n=r(64554),d=r(20068),l=r(13400),m=r(86584),h=r(29464),u=r(19035),p=r(80184);t.default=()=>{const[e,t]=(0,a.useState)(!1),[r,v]=(0,a.useState)(""),[f,y]=(0,a.useState)([]);(0,a.useEffect)((()=>(b(),()=>{y([])})),[]);const b=async()=>{try{const e=await(0,u.A_)("/estimate/receipt");y(e)}catch(e){console.error(e.message)}},g=(0,a.useMemo)((()=>[{accessorKey:"receiptId",header:"Id",size:50},{accessorKey:"amount",header:"Receipt Amt",size:50},{accessorKey:"estimateIdList",header:"Estimate Ids",size:100,Cell:e=>{var t;let{row:r}=e;return(null===(t=r.original.estimateIdList)||void 0===t?void 0:t.join(", "))||"N/A"}},{accessorKey:"paymentMode",header:"Payment Mode",size:50},{accessorKey:"comment",header:"Comment",size:100}]),[]),w=(0,o.Z)(),z=(0,a.useMemo)((()=>(0,c.Z)({palette:{mode:w.palette.mode,primary:w.palette.secondary,info:{main:"rgb(255,122,0)"},background:{default:"rgba(0, 0, 0, 0)"}},typography:{button:{textTransform:"none",fontSize:"1.2rem"}},components:{MuiTooltip:{styleOverrides:{tooltip:{fontSize:"1.1rem"}}},MuiSwitch:{styleOverrides:{thumb:{color:"pink"}}}}})),[w]);return(0,p.jsxs)(p.Fragment,{children:[e&&(0,p.jsx)(h.Z,{showAlert:e,setShowAlert:t,alertColor:"info",alertMess:r}),(0,p.jsx)(i.Z,{theme:z,children:(0,p.jsx)(s.P2,{columns:g,data:f,enableFacetedValues:!0,enableEditing:!0,muiTablePaperProps:{elevation:0,sx:{borderRadius:"0",background:"linear-gradient(195deg, #e2d7d5, #cf8989)"}},renderRowActions:e=>{let{row:r}=e;return(0,p.jsx)(n.Z,{sx:{display:"flex",gap:"1rem"},children:(0,p.jsx)(d.Z,{arrow:!0,placement:"right",title:"Receipt PDF",children:(0,p.jsx)(l.Z,{onClick:()=>{(async e=>{try{const r=await(0,u.$R)("/estimate/receiptPdf/"+e.id),a=window.URL.createObjectURL(r),s=document.createElement("a");s.href=a,s.setAttribute("download","Receipt_"+e.receiptId+".pdf"),document.body.appendChild(s),s.click(),s.parentNode.removeChild(s),v("Generated Estimate. Check downloads"),t(!0)}catch(r){onClose(),console.log(r.message),v(r.message),t(!0)}})(r.original)},children:(0,p.jsx)(m.Z,{})})})})}})})]})}},86584:function(e,t,r){var a=r(76189),s=r(80184);t.Z=(0,a.Z)((0,s.jsx)("path",{d:"M20 2H8c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-8.5 7.5c0 .83-.67 1.5-1.5 1.5H9v2H7.5V7H10c.83 0 1.5.67 1.5 1.5v1zm5 2c0 .83-.67 1.5-1.5 1.5h-2.5V7H15c.83 0 1.5.67 1.5 1.5v3zm4-3H19v1h1.5V11H19v2h-1.5V7h3v1.5zM9 9.5h1v-1H9v1zM4 6H2v14c0 1.1.9 2 2 2h14v-2H4V6zm10 5.5h1v-3h-1v3z"}),"PictureAsPdf")}}]);
//# sourceMappingURL=8158.335e157f.chunk.js.map